package it.alnao.aws.s3console.utils;

// S3CommanderException.java
public class S3CommanderException extends RuntimeException {
    public S3CommanderException(String message) {
        super(message);
    }

    public S3CommanderException(String message, Throwable cause) {
        super(message, cause);
    }
}