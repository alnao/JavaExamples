package it.alnao.javaexamples.awssdk.service;

import it.alnao.javaexamples.awssdk.aws.ec2.Ec2Manager;
import it.alnao.javaexamples.awssdk.aws.ec2.KeyPairManager;
import it.alnao.javaexamples.awssdk.aws.ec2.SecurityGroupManager;
import it.alnao.javaexamples.awssdk.restModel.ec2.Ec2CreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Ec2Service {

    private final Ec2Manager ec2Manager;
    private final SecurityGroupManager sgManager;
    private final KeyPairManager keyPairManager;

    // Espone direttamente i metodi dei componenti
    public String createInstance(Ec2CreateRequest req) {
        return ec2Manager.createInstance(req);
    }

    public String terminateInstance(String region,String instanceId) {
        return ec2Manager.terminateInstance(region,instanceId);
    }

    public Object listInstances(String region) {
        return ec2Manager.listInstances(region);
    }

    public String stopInstance(String region,String instanceId) {
        return ec2Manager.stopInstance(region,instanceId);
    }

    public String startInstance(String region,String instanceId) {
        return ec2Manager.startInstance(region,instanceId);
    }

    public Object listSecurityGroups() {
        return sgManager.listSecurityGroups();
    }

    public String authorizeSecurityGroupIngress(String groupId) {
        return sgManager.authorizeIngress(groupId);
    }

    public String revokeSecurityGroupIngress(String groupId) {
        return sgManager.revokeIngress(groupId);
    }

    public Object listKeyPairs() {
        return keyPairManager.listKeyPairs();
    }
}
