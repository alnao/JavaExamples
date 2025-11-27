package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.GetRestApisResponse;
import software.amazon.awssdk.services.apigateway.model.RestApi;

import java.util.List;

/**
 * Servizio per la gestione delle API Gateway
 */
public class ApiGatewayService {
    private ApiGatewayClient apiGatewayClient;

    public ApiGatewayService(Region region, String profile) {
        this.apiGatewayClient = ApiGatewayClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.apiGatewayClient != null) {
            this.apiGatewayClient.close();
        }
        this.apiGatewayClient = ApiGatewayClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<RestApi> getApis() {
        GetRestApisResponse response = apiGatewayClient.getRestApis();
        return response.items();
    }

    public void close() {
        if (apiGatewayClient != null) {
            apiGatewayClient.close();
        }
    }
}
