package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import java.util.List;

/**
 * Servizio per la gestione dei bucket S3
 */
public class S3Service {
    private S3Client s3Client;

    public S3Service(Region region, String profile) {
        this.s3Client = S3Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.s3Client != null) {
            this.s3Client.close();
        }
        this.s3Client = S3Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<Bucket> getBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        return response.buckets();
    }

    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }
}
