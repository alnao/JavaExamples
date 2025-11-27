package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;

import java.util.List;

/**
 * Servizio per la gestione delle code SQS
 */
public class SqsService {
    private SqsClient sqsClient;

    public SqsService(Region region, String profile) {
        this.sqsClient = SqsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.sqsClient != null) {
            this.sqsClient.close();
        }
        this.sqsClient = SqsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<String> getQueues() {
        ListQueuesResponse response = sqsClient.listQueues();
        return response.queueUrls();
    }

    public void close() {
        if (sqsClient != null) {
            sqsClient.close();
        }
    }
}
