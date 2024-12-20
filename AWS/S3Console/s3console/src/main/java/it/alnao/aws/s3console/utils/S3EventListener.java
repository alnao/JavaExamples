package it.alnao.aws.s3console.utils;

@FunctionalInterface
public interface S3EventListener {
    void onS3Event(S3Event event);
}