package it.alnao.javaexamples.awssdk.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
//import software.amazon.awssdk.services.sqs.SqsClient;
//import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsServicesConfig {

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider, Region region) {
        return S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
    }
/*
    @Bean
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider credentialsProvider, Region region) {
        return DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public SqsClient sqsClient(AwsCredentialsProvider credentialsProvider, Region region) {
        return SqsClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public SnsClient snsClient(AwsCredentialsProvider credentialsProvider, Region region) {
        return SnsClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
    }
 */
}
