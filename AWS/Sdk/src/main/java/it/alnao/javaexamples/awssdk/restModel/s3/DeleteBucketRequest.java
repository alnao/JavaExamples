package it.alnao.javaexamples.awssdk.restModel.s3;

//import software.amazon.awssdk.regions.Region;

public class DeleteBucketRequest {
    private String bucketName;

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

}