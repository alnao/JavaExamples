package it.alnao.javaexamples.awssdk.restModel.ec2;

import lombok.Data;

@Data
public class Ec2CreateRequest {
    private String region;
    private String amiId;
    private String instanceType;
    private String keyName;
    private String securityGroupId;
    
}