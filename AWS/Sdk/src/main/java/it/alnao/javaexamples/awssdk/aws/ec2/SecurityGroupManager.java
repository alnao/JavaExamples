package it.alnao.javaexamples.awssdk.aws.ec2;

import it.alnao.javaexamples.awssdk.config.Ec2ClientFactory;
import it.alnao.javaexamples.awssdk.utils.RegionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityGroupManager {

    private final Ec2ClientFactory clientFactory;

    private Ec2Client getEc2Client(String region){
        return clientFactory.getClient(RegionUtils.getRegionOrDefault( region) );
    }
    public List<SecurityGroup> listSecurityGroups() {
        Ec2Client ec2Client = getEc2Client( null );
        return ec2Client.describeSecurityGroups().securityGroups();
    }

    public String authorizeIngress(String groupId) {
        Ec2Client ec2Client = getEc2Client( null );
        IpPermission permission = IpPermission.builder()
                .ipProtocol("tcp")
                .fromPort(22)
                .toPort(22)
                .ipRanges(IpRange.builder().cidrIp("0.0.0.0/0").build())
                .build();

        AuthorizeSecurityGroupIngressRequest request = AuthorizeSecurityGroupIngressRequest.builder()
                .groupId(groupId)
                .ipPermissions(permission)
                .build();

        ec2Client.authorizeSecurityGroupIngress(request);
        return "Ingress rule added to " + groupId;
    }

    public String revokeIngress(String groupId) {
        Ec2Client ec2Client = getEc2Client( null );
        IpPermission permission = IpPermission.builder()
                .ipProtocol("tcp")
                .fromPort(22)
                .toPort(22)
                .ipRanges(IpRange.builder().cidrIp("0.0.0.0/0").build())
                .build();

        RevokeSecurityGroupIngressRequest request = RevokeSecurityGroupIngressRequest.builder()
                .groupId(groupId)
                .ipPermissions(permission)
                .build();

        ec2Client.revokeSecurityGroupIngress(request);
        return "Ingress rule removed from " + groupId;
    }
}
