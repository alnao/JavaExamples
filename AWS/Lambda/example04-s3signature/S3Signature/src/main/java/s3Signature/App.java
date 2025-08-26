package s3Signature;

import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> errorHeaders = new HashMap<>();
        errorHeaders.put("Content-Type", "application/json");

        // Leggi bucket dalla variabile di ambiente (con default)
        String bucket = System.getenv("S3_BUCKET_NAME");
        if (bucket == null || bucket.isEmpty()) {
            return new APIGatewayProxyResponseEvent()
                .withHeaders(errorHeaders)
                .withStatusCode(400)
                .withBody("{\"error\": \"Missing required parameter 'bucket name'\"}");
        }
        
        // Leggi objectKey dai query parameters
        String objectKey = null;
        if (input.getQueryStringParameters() != null) {
            objectKey = input.getQueryStringParameters().get("key");
        }
        
        // Valida che objectKey sia fornito
        if (objectKey == null || objectKey.isEmpty()) {

            return new APIGatewayProxyResponseEvent()
                .withHeaders(errorHeaders)
                .withStatusCode(400)
                .withBody("{\"error\": \"Missing required parameter 'key'\"}");
        }
        
        // Leggi durata di scadenza dai query parameters (default 1 ora)
        int expirationHours = 1;
        if (input.getQueryStringParameters() != null && input.getQueryStringParameters().get("expires") != null) {
            try {
                expirationHours = Integer.parseInt(input.getQueryStringParameters().get("expires"));
                // Limita a massimo 24 ore per sicurezza
                if (expirationHours > 24) {
                    expirationHours = 24;
                }
            } catch (NumberFormatException e) {
                context.getLogger().log("Invalid expires parameter, using default 1 hour");
            }
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            //AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());        
            
            // Leggi la regione dalla variabile d'ambiente (con fallback a EU_CENTRAL_1)
            String regionName = System.getenv("AWS_REGION_NAME");
            Regions clientRegion = Regions.EU_CENTRAL_1; // default fallback
            
            if (regionName != null && !regionName.isEmpty()) {
                try {
                    clientRegion = Regions.fromName(regionName);
                    context.getLogger().log("Using region from environment: " + regionName);
                } catch (Exception e) {
                    context.getLogger().log("Invalid region in environment (" + regionName + "), using default: eu-central-1");
                }
            } else {
                context.getLogger().log("No region specified in environment, using default: eu-central-1");
            }
            
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    //.withCredentials(DefaultAWSCredentialsProviderChain.getInstance()) // new ProfileCredentialsProvider())
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .build();
            //System.out.println( DefaultAWSCredentialsProviderChain.getInstance().getCredentials().getAWSAccessKeyId() );
            
            // Set the presigned URL to expire after specified hours
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60 * expirationHours; // Convert hours to milliseconds
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL
            context.getLogger().log("Generating pre-signed URL for bucket: " + bucket + ", key: " + objectKey);
            URL url = s3Client.generatePresignedUrl(bucket, objectKey, expiration);

            context.getLogger().log("Pre-Signed URL generated successfully");
            
            // Crea response JSON strutturato
            String jsonResponse = String.format(
                "{\"presignedUrl\": \"%s\", \"bucket\": \"%s\", \"key\": \"%s\", \"region\": \"%s\", \"expiresInHours\": %d, \"expirationTime\": \"%s\"}",
                url.toString(), bucket, objectKey, clientRegion.getName(), expirationHours, expiration.toString()
            );
            
            return response
                    .withStatusCode(200)
                    .withBody(jsonResponse);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            context.getLogger().log("Amazon S3 error: " + e.getMessage());
            return response.withStatusCode(500)
                .withBody("{\"error\": \"S3 service error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            context.getLogger().log("General error: " + e.getMessage());
            return response.withStatusCode(500)
                .withBody("{\"error\": \"Internal server error: " + e.getMessage() + "\"}");
        }

        /* 
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);

            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (IOException e) {
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
        */
    }
/*
    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
 */

}
