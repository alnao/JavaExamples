package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.ListStateMachinesResponse;
import software.amazon.awssdk.services.sfn.model.StateMachineListItem;

import java.util.List;

/**
 * Servizio per la gestione delle Step Functions
 */
public class StepFunctionsService {
    private SfnClient sfnClient;

    public StepFunctionsService(Region region, String profile) {
        this.sfnClient = SfnClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.sfnClient != null) {
            this.sfnClient.close();
        }
        this.sfnClient = SfnClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<StateMachineListItem> getStateMachines() {
        ListStateMachinesResponse response = sfnClient.listStateMachines();
        return response.stateMachines();
    }

    public void close() {
        if (sfnClient != null) {
            sfnClient.close();
        }
    }
}
