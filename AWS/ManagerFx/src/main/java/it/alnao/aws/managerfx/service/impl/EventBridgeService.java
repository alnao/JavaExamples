package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.ListRulesResponse;
import software.amazon.awssdk.services.eventbridge.model.Rule;

import java.util.List;

/**
 * Servizio per la gestione delle regole EventBridge
 */
public class EventBridgeService {
    private EventBridgeClient eventBridgeClient;

    public EventBridgeService(Region region, String profile) {
        this.eventBridgeClient = EventBridgeClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(Region region, String profile) {
        if (this.eventBridgeClient != null) {
            this.eventBridgeClient.close();
        }
        this.eventBridgeClient = EventBridgeClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<Rule> getRules() {
        ListRulesResponse response = eventBridgeClient.listRules(r -> r.build());
        return response.rules();
    }

    public void close() {
        if (eventBridgeClient != null) {
            eventBridgeClient.close();
        }
    }
}
