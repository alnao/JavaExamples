package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.DistributionSummary;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsResponse;

import java.util.List;

/**
 * Servizio per la gestione delle distribuzioni CloudFront
 */
public class CloudFrontService {
    private CloudFrontClient cloudFrontClient;

    public CloudFrontService(String profile) {
        // CloudFront è un servizio globale
        this.cloudFrontClient = CloudFrontClient.builder()
            .region(Region.AWS_GLOBAL)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(String profile) {
        if (this.cloudFrontClient != null) {
            this.cloudFrontClient.close();
        }
        this.cloudFrontClient = CloudFrontClient.builder()
            .region(Region.AWS_GLOBAL)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<DistributionSummary> getDistributions() {
        ListDistributionsResponse response = cloudFrontClient.listDistributions();
        return response.distributionList().items();
    }

    public void close() {
        if (cloudFrontClient != null) {
            cloudFrontClient.close();
        }
    }
}
