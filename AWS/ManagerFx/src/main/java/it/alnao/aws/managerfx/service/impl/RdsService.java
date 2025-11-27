package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;

import java.util.List;

/**
 * Servizio per la gestione delle istanze RDS
 */
public class RdsService {
    private RdsClient rdsClient;

    public RdsService(Region region, String profile) {
        this.rdsClient = RdsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.rdsClient != null) {
            this.rdsClient.close();
        }
        this.rdsClient = RdsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<DBInstance> getInstances() {
        DescribeDbInstancesResponse response = rdsClient.describeDBInstances();
        return response.dbInstances();
    }

    public void close() {
        if (rdsClient != null) {
            rdsClient.close();
        }
    }
}
