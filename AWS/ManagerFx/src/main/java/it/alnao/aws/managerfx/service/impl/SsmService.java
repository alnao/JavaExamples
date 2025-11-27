package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.DescribeParametersResponse;
import software.amazon.awssdk.services.ssm.model.ParameterMetadata;

import java.util.List;

/**
 * Servizio per la gestione dei parametri SSM
 */
public class SsmService {
    private SsmClient ssmClient;

    public SsmService(Region region, String profile) {
        this.ssmClient = SsmClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.ssmClient != null) {
            this.ssmClient.close();
        }
        this.ssmClient = SsmClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<ParameterMetadata> getParameters() {
        DescribeParametersResponse response = ssmClient.describeParameters();
        return response.parameters();
    }

    public void close() {
        if (ssmClient != null) {
            ssmClient.close();
        }
    }
}
