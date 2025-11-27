package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

/**
 * Servizio per la gestione delle risorse EC2
 */
public class Ec2Service {
    private Ec2Client ec2Client;

    public Ec2Service(Region region, String profile) {
        this.ec2Client = Ec2Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.ec2Client != null) {
            this.ec2Client.close();
        }
        this.ec2Client = Ec2Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public Vpc getDefaultVpc() {
        DescribeVpcsResponse response = ec2Client.describeVpcs(
            DescribeVpcsRequest.builder()
                .filters(Filter.builder().name("isDefault").values("true").build())
                .build()
        );
        return response.vpcs().isEmpty() ? null : response.vpcs().get(0);
    }

    public List<Subnet> getSubnets(String vpcId) {
        DescribeSubnetsResponse response = ec2Client.describeSubnets(
            DescribeSubnetsRequest.builder()
                .filters(Filter.builder().name("vpc-id").values(vpcId).build())
                .build()
        );
        return response.subnets();
    }

    public List<Instance> getInstances() {
        DescribeInstancesResponse response = ec2Client.describeInstances();
        return response.reservations().stream()
            .flatMap(reservation -> reservation.instances().stream())
            .toList();
    }

    public List<SecurityGroup> getSecurityGroups() {
        DescribeSecurityGroupsResponse response = ec2Client.describeSecurityGroups();
        return response.securityGroups();
    }

    public void close() {
        if (ec2Client != null) {
            ec2Client.close();
        }
    }
}
