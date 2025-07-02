package it.alnao.javaexamples.awssdk.aws.s3;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
public class S3Content {
    private static final Logger logger = LoggerFactory.getLogger(S3Content.class);

    private final S3Client s3Client;

    public S3Content() {
        this.s3Client = S3Client.create(); // Usa config da default provider chain
    }
    public List<String> listBuckets() {
        return s3Client.listBuckets()
                .buckets()
                .stream()
                .map(Bucket::name)
                .collect(Collectors.toList());
    }
    public List<String> listFiles(String bucketName, String prefix) {
        logger.info("S3Content.listFiles {} {}",bucketName,prefix);
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .delimiter("/")
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(request);

        List<String> items = new ArrayList<>();

        if (result.hasCommonPrefixes()) {
            for (CommonPrefix cp : result.commonPrefixes()) {
                items.add(cp.prefix()); // directory simulate
            }
        }

        if (result.hasContents()) {
            for (S3Object obj : result.contents()) {
                if (!obj.key().equals(prefix)) { // evitare il "finto oggetto" directory
                    items.add(obj.key());
                }
            }
        }

        return items;
    }

    public String deleteFile(String bucketName, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
        return key;
    }

    public InputStream downloadFile(String bucketName, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request);
        return s3Object; // ritorna stream da leggere
    }
}
