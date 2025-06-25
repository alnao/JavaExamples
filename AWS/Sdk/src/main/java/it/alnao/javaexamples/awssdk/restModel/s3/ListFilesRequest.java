package it.alnao.javaexamples.awssdk.restModel.s3;

public class ListFilesRequest {
    private String bucketName;
    private String prefix; // può essere null o ""

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getPrefix() { return prefix == null ? "" : prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
}