package it.alnao.javaexamples.awssdk.controller;

import it.alnao.javaexamples.awssdk.restModel.ec2.Ec2CreateRequest;
import it.alnao.javaexamples.awssdk.service.Ec2Service;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ec2")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Ec2Controller {

    @Autowired
    private final Ec2Service service;

    @PostMapping("/create")
    public String create(@RequestBody Ec2CreateRequest request) {
        return service.createInstance(request);
    }

    @DeleteMapping("/terminate/{region}/{instanceId}")
    public String terminate(@PathVariable String region,@PathVariable String instanceId) {
        return service.terminateInstance(region,instanceId);
    }

    @GetMapping("/list/{region}")
    public Object list(@PathVariable String region) {
        return service.listInstances(region);
    }

    @PostMapping("/stop/{region}/{instanceId}")
    public String stop(@PathVariable String region,@PathVariable String instanceId) {
        return service.stopInstance(region,instanceId);
    }

    @PostMapping("/start/{region}/{instanceId}")
    public String start(@PathVariable String region,@PathVariable String instanceId) {
        return service.startInstance(region,instanceId);
    }

    @GetMapping("/security-groups")
    public Object listSG() {
        return service.listSecurityGroups();
    }

    @PostMapping("/security-groups/{groupId}/authorize")
    public String authSG(@PathVariable String groupId) {
        return service.authorizeSecurityGroupIngress(groupId);
    }

    @PostMapping("/security-groups/{groupId}/revoke")
    public String revokeSG(@PathVariable String groupId) {
        return service.revokeSecurityGroupIngress(groupId);
    }

    @GetMapping("/key-pairs")
    public Object listKeys() {
        return service.listKeyPairs();
    }
}
