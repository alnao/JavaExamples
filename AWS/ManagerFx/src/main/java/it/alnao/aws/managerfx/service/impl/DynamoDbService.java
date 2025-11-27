package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import java.util.List;

/**
 * Servizio per la gestione delle tabelle DynamoDB
 */
public class DynamoDbService {
    private DynamoDbClient dynamoDbClient;

    public DynamoDbService(Region region, String profile) {
        this.dynamoDbClient = DynamoDbClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.dynamoDbClient != null) {
            this.dynamoDbClient.close();
        }
        this.dynamoDbClient = DynamoDbClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<String> getTables() {
        ListTablesResponse response = dynamoDbClient.listTables();
        return response.tableNames();
    }

    public void close() {
        if (dynamoDbClient != null) {
            dynamoDbClient.close();
        }
    }
}
