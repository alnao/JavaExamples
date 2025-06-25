package it.alnao.javaexamples.awssdk.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.common.util.StringUtils;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AwsConfig {

    private final AwsProperties awsProperties;

    public AwsConfig(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if ( StringUtils.isNotEmpty(awsProperties.getAccessKey()) && StringUtils.isNotEmpty(awsProperties.getSecretKey()) ){
        //if (awsProperties.getAccessKey() != null && awsProperties.getSecretKey() != null) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                            awsProperties.getAccessKey(),
                            awsProperties.getSecretKey()));
        } else if (awsProperties.getProfile() != null) {
            return ProfileCredentialsProvider.create(awsProperties.getProfile());
        } else {
            return DefaultCredentialsProvider.create();
        }
    }

    @Bean
    public Region awsRegion() {
        return Region.of(awsProperties.getRegion());
    }
}