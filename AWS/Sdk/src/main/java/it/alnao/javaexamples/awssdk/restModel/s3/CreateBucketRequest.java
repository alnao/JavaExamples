package it.alnao.javaexamples.awssdk.restModel.s3;

public class CreateBucketRequest {
    private String bucketName;
    private String region;
    private boolean enableVersioning;
    private boolean enableEventBridge;
    private boolean enableServerSideEncryption;

    // Getters e Setters
    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public boolean isEnableVersioning() { return enableVersioning; }
    public void setEnableVersioning(boolean enableVersioning) { this.enableVersioning = enableVersioning; }

    public boolean isEnableEventBridge() { return enableEventBridge; }
    public void setEnableEventBridge(boolean enableEventBridge) { this.enableEventBridge = enableEventBridge; }

    public boolean isEnableServerSideEncryption() { return enableServerSideEncryption; }
    public void setEnableServerSideEncryption(boolean enableServerSideEncryption) { this.enableServerSideEncryption = enableServerSideEncryption; }
}
