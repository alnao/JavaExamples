package it.alnao.aws.managerfx.service.impl;

import it.alnao.aws.managerfx.model.DefaultTags;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Tag;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servizio per la gestione dei bucket S3
 */
public class S3Service {
    private S3Client s3Client;
    private String profile;
    // client per regione: i tag di un bucket vanno letti nella regione in cui si trova il bucket
    private final Map<Region, S3Client> regionalClients = new ConcurrentHashMap<>();

    public S3Service(Region region, String profile) {
        this.profile = profile;
        this.s3Client = buildClient(region);
    }

    private S3Client buildClient(Region region) {
        return S3Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        close();
        this.profile = profile;
        this.s3Client = buildClient(region);
    }

    public List<Bucket> getBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        return response.buckets();
    }

    /**
     * Tag del bucket (permessi IAM richiesti: s3:GetBucketLocation, s3:GetBucketTagging).
     * Restituisce una mappa vuota se il bucket non ha tag, {@link DefaultTags#ERROR} in caso di errore.
     */
    public Map<String, String> getBucketTags(String bucketName) {
        try {
            S3Client client = regionalClients.computeIfAbsent(getBucketRegion(bucketName), this::buildClient);
            return DefaultTags.toMap(client.getBucketTagging(r -> r.bucket(bucketName)).tagSet(), Tag::key, Tag::value);
        } catch (S3Exception e) {
            if (e.awsErrorDetails() != null && "NoSuchTagSet".equals(e.awsErrorDetails().errorCode())) {
                return Map.of();
            }
            System.err.println("[S3Service] tag non leggibili per " + bucketName + ": " + e.getMessage());
            return DefaultTags.ERROR;
        } catch (Exception e) {
            System.err.println("[S3Service] tag non leggibili per " + bucketName + ": " + e.getMessage());
            return DefaultTags.ERROR;
        }
    }

    private Region getBucketRegion(String bucketName) {
        // GetBucketLocation va chiamato sull'endpoint us-east-1, valido per i bucket di tutte le regioni
        S3Client locationClient = regionalClients.computeIfAbsent(Region.US_EAST_1, this::buildClient);
        String location = locationClient.getBucketLocation(r -> r.bucket(bucketName)).locationConstraintAsString();
        if (location == null || location.isEmpty()) {
            return Region.US_EAST_1;
        }
        if ("EU".equals(location)) {
            return Region.EU_WEST_1;
        }
        return Region.of(location);
    }

    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
        regionalClients.values().forEach(S3Client::close);
        regionalClients.clear();
    }
}
