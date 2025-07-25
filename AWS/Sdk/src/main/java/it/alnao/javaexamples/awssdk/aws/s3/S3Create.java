package it.alnao.javaexamples.awssdk.aws.s3;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketCannedACL;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.VersioningConfiguration;
import software.amazon.awssdk.services.s3.model.BucketVersioningStatus;
import software.amazon.awssdk.services.s3.model.PutBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionConfiguration;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionRule;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionByDefault;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.NotificationConfiguration;
import software.amazon.awssdk.services.s3.model.EventBridgeConfiguration;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import it.alnao.javaexamples.awssdk.utils.RegionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class S3Create {
    private static final Logger logger = LoggerFactory.getLogger(S3Create.class);
    private static final String DEFAULT_BUCKET_NAME = System.getenv("DEFAULT_BUCKET_NAME");
    private static final Region DEFAULT_REGION = RegionUtils.getRegionOrDefault(System.getenv("AWS_REGION"));

    public static void main(String[] args) throws Exception {
        logger.info("S3 Main started");
        
        String bucketName = DEFAULT_BUCKET_NAME;
        if (args.length > 0) {
            bucketName = args[0]; // Permette di passare il nome del bucket come argomento da riga di comando
        }
        Region region = DEFAULT_REGION; // Esempio: Cambia con la tua regione preferita

        S3Create s3Create=new S3Create();
        String bucketArn=s3Create.createBucket(bucketName,region,false,true,false);

        logger.info("S3 Main complete with {}",bucketArn);
    }

    public String createBucket(String bucketName,Region region, boolean enableVersioning, boolean enableEventBridge, boolean enableServerSideEncryption) throws S3Exception {
        validateBucketName(bucketName);
        
        try (S3Client s3Client = S3Client.builder().region(region).build()) {
            logger.info("Initializing S3Client for region: {} {}", bucketName,region.id());
            // Configura la regione AWS. Assicurati che le tue credenziali siano configurate (es. variabili d'ambiente, ~/.aws/credentials)
 
            logger.info("Attempting to create S3 bucket: " + bucketName);

            // 1. Creazione del Bucket
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .acl(BucketCannedACL.PRIVATE) // AccessControl: Private
                    .build();

            CreateBucketResponse createResponse = s3Client.createBucket(createBucketRequest);
            logger.info("Bucket '" + bucketName + "' created successfully.");
            logger.debug(createResponse.toString());

            // 2. Abilitazione del Versioning
        if (enableVersioning){
            PutBucketVersioningRequest putVersioningRequest = PutBucketVersioningRequest.builder()
                    .bucket(bucketName)
                    .versioningConfiguration(VersioningConfiguration.builder()
                            .status(BucketVersioningStatus.ENABLED)
                            .build())
                    .build();
            s3Client.putBucketVersioning(putVersioningRequest);
            logger.info("Versioning enabled for bucket '" + bucketName + "'.");
        }else{
                logger.info("Versioning not enabled");
        }

            // 3. Configurazione della crittografia lato server (AES256)
        if (enableServerSideEncryption){
            ServerSideEncryptionByDefault encryptionByDefault = ServerSideEncryptionByDefault.builder()
                    .sseAlgorithm(ServerSideEncryption.AES256)
                    .build();
            ServerSideEncryptionRule encryptionRule = ServerSideEncryptionRule.builder()
                    .applyServerSideEncryptionByDefault(encryptionByDefault)
                    .build();
            ServerSideEncryptionConfiguration encryptionConfiguration = ServerSideEncryptionConfiguration.builder()
                    .rules(encryptionRule)
                    .build();
            PutBucketEncryptionRequest putEncryptionRequest = PutBucketEncryptionRequest.builder()
                    .bucket(bucketName)
                    .serverSideEncryptionConfiguration(encryptionConfiguration)
                    .build();
            s3Client.putBucketEncryption(putEncryptionRequest);
            logger.info("Server-side encryption (AES256) enabled for bucket '" + bucketName + "'.");
        }else{
                logger.info("Server-side not enabled");
        }
            // 4. Abilitazione di EventBridge
        if (enableEventBridge){
            EventBridgeConfiguration eventBridgeConfig = EventBridgeConfiguration.builder()
//                    .eventBridgeEnabled(true)
                    .build();
            NotificationConfiguration notificationConfig = NotificationConfiguration.builder()
                    .eventBridgeConfiguration(eventBridgeConfig)
                    .build();
            PutBucketNotificationConfigurationRequest putNotificationRequest = PutBucketNotificationConfigurationRequest.builder()
                    .bucket(bucketName)
                    .notificationConfiguration(notificationConfig)
                    .build();
            s3Client.putBucketNotificationConfiguration(putNotificationRequest);
            logger.info("EventBridge integration enabled for bucket '" + bucketName + "'.");
        }else{
                logger.info("EventBridge integration not enabled");
        }
            // --- Outputs ---
            logger.info("\n--- Outputs ---");
            logger.info("BucketName: " + bucketName);

            // Ottenere l'ARN del bucket in SDK è un po' meno diretto di CloudFormation/CDK.
            // L'ARN ha il formato "arn:aws:s3:::<bucket-name>"
            String bucketArn = "arn:aws:s3:::" + bucketName;
            logger.info("BucketARN: " + bucketArn);

            // Lo StackName non ha un equivalente diretto nell'SDK quando si crea risorse in modo imperativo.
            // Lo StackName è un concetto di CloudFormation.
            // Se si esegue questo codice all'interno di un'applicazione che è parte di uno stack CloudFormation
            // si potrebbe recuperarlo dalle variabili d'ambiente o altre configurazioni, ma non è intrinseco alla creazione SDK.
            logger.info("StackName: N/A (concetto di CloudFormation, non SDK diretto)");
                return bucketArn;
        } catch (S3Exception e) {
            logger.error("Error creating S3 bucket: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void validateBucketName(String bucketName) {
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("Bucket name cannot be null or empty");
        }
        // Add more validation as per AWS bucket naming rules
    }

    public String deleteBucketIfEmpty(String bucketName/* ,Region region*/) {
        logger.info("Initializing S3Client for region: {} ", bucketName /* ,region.id()*/);
        S3Client s3Client = S3Client.builder()
//                .region(region)
                .build();

        // Controlla se il bucket è vuoto
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .maxKeys(1) // ci basta sapere se ha almeno 1 oggetto
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        if (!listResponse.contents().isEmpty()) {
                return "Il bucket non \u00E8 vuoto. Nessuna azione eseguita.";
        }

        // Elimina il bucket
        DeleteBucketRequest deleteRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3Client.deleteBucket(deleteRequest);
        return bucketName;
   }
}