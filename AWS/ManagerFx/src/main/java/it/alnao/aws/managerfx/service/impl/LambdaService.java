package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;

import java.util.List;

/**
 * Servizio per la gestione delle funzioni Lambda
 */
public class LambdaService {
    private LambdaClient lambdaClient;

    public LambdaService(Region region, String profile) {
        this.lambdaClient = LambdaClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.lambdaClient != null) {
            this.lambdaClient.close();
        }
        this.lambdaClient = LambdaClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<FunctionConfiguration> getFunctions() {
        ListFunctionsResponse response = lambdaClient.listFunctions();
        return response.functions();
    }

    public void close() {
        if (lambdaClient != null) {
            lambdaClient.close();
        }
    }
}
