package it.alnao.javaexamples.awssdk.aws.ec2;

import it.alnao.javaexamples.awssdk.restModel.ec2.Ec2CreateRequest;
import it.alnao.javaexamples.awssdk.config.Ec2ClientFactory;
import it.alnao.javaexamples.awssdk.utils.RegionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Ec2Manager {

    //private final Ec2Client ec2Client;
    private final Ec2ClientFactory clientFactory;

    private Ec2Client getEc2Client(String region){
        return clientFactory.getClient(RegionUtils.getRegionOrDefault( region) );
    }

    public String createInstance(Ec2CreateRequest req) {
        Ec2Client ec2Client = getEc2Client(req.getRegion() );
        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId(req.getAmiId())
                .instanceType(InstanceType.fromValue(req.getInstanceType()))
                .minCount(1)
                .maxCount(1)
                .keyName(req.getKeyName())
                .securityGroupIds(req.getSecurityGroupId())
                .build();

        RunInstancesResponse response = ec2Client.runInstances(request);
        return response.instances().get(0).instanceId();
    }

    public String terminateInstance(String region, String instanceId) {
        TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                .instanceIds(instanceId).build();
        Ec2Client ec2Client = getEc2Client(region );
        ec2Client.terminateInstances(request);
        return "Terminated: " + instanceId;
    }

    public List<Reservation> listInstances(String region) {
        Ec2Client ec2Client = getEc2Client(region );
        return ec2Client.describeInstances().reservations();
    }

    public String stopInstance(String region, String instanceId) {
        Ec2Client ec2Client = getEc2Client(region );
        ec2Client.stopInstances(StopInstancesRequest.builder()
                .instanceIds(instanceId).build());
        return "Stopped: " + instanceId;
    }

    public String startInstance(String region, String instanceId) {
        Ec2Client ec2Client = getEc2Client(region);
        ec2Client.startInstances(StartInstancesRequest.builder()
                .instanceIds(instanceId).build());
        return "Started: " + instanceId;
    }
}
