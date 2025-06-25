package it.alnao.javaexamples.awssdk.service;


import it.alnao.javaexamples.awssdk.aws.s3.S3Content;
import it.alnao.javaexamples.awssdk.aws.s3.S3Create;
import software.amazon.awssdk.regions.Region;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class S3Service {
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private S3Create s3Create;
    @Autowired
    private S3Content s3Content;

    public List<String> listFiles(String bucketName, String prefix) {
        logger.info("S3Service.listFiles {} {}",bucketName,prefix);
        return s3Content.listFiles(bucketName, prefix);
    }

    public String deleteFile(String bucketName, String key) {
        return s3Content.deleteFile(bucketName, key);
    }

    public InputStream downloadFile(String bucketName, String key) {
        return s3Content.downloadFile(bucketName, key);
    }

    public List<String> listBuckets() {
        return s3Content.listBuckets();
    }

    public String deleteBucketIfEmpty(String bucketName) {
        logger.info("S3Service.deleteBucketIfEmpty {}",bucketName);
        return s3Create.deleteBucketIfEmpty(bucketName);
    }

    public String createBucket(String bucketName,Region region, boolean enableVersioning, boolean enableEventBridge, boolean enableServerSideEntryption) {
        logger.info("S3Service.createBucket {}",bucketName);
        return s3Create.createBucket(bucketName, region, enableVersioning, enableEventBridge, enableServerSideEntryption);
    }

}
