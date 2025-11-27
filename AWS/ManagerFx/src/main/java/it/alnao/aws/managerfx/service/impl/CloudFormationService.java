package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.ListStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.StackSummary;

import java.util.List;

/**
 * Servizio per la gestione degli stack CloudFormation
 */
public class CloudFormationService {
    private CloudFormationClient cfnClient;

    public CloudFormationService(Region region, String profile) {
        this.cfnClient = CloudFormationClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.cfnClient != null) {
            this.cfnClient.close();
        }
        this.cfnClient = CloudFormationClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<StackSummary> getStacks() {
        ListStacksResponse response = cfnClient.listStacks();
        return response.stackSummaries();
    }

    public void close() {
        if (cfnClient != null) {
            cfnClient.close();
        }
    }
}
