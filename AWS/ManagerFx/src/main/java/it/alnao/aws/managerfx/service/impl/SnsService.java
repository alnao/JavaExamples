package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.Topic;

import java.util.List;

/**
 * Servizio per la gestione dei topic SNS
 */
public class SnsService {
    private SnsClient snsClient;

    public SnsService(Region region, String profile) {
        this.snsClient = SnsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.snsClient != null) {
            this.snsClient.close();
        }
        this.snsClient = SnsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<Topic> getTopics() {
        ListTopicsResponse response = snsClient.listTopics();
        return response.topics();
    }

    public void close() {
        if (snsClient != null) {
            snsClient.close();
        }
    }
}
