package it.alnao.javaexamples.awssdk.restModel.s3;

public class DownloadFileRequest {
    private String bucketName;
    private String key;

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}