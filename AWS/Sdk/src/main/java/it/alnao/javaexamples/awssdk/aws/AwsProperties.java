package it.alnao.javaexamples.awssdk.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String profile;

    // Getters e Setters
    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
}