package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;

import java.util.List;

/**
 * Servizio per la gestione delle risorse IAM
 */
public class IamService {
    private IamClient iamClient;

    public IamService(String profile) {
        // IAM è un servizio globale, usa sempre us-east-1
        this.iamClient = IamClient.builder()
            .region(Region.AWS_GLOBAL)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public void updateConfiguration(String profile) {
        if (this.iamClient != null) {
            this.iamClient.close();
        }
        this.iamClient = IamClient.builder()
            .region(Region.AWS_GLOBAL)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    public List<Group> getGroups() {
        ListGroupsResponse response = iamClient.listGroups();
        return response.groups();
    }

    public List<User> getUsers() {
        ListUsersResponse response = iamClient.listUsers();
        return response.users();
    }

    public List<Role> getRoles() {
        ListRolesResponse response = iamClient.listRoles();
        return response.roles();
    }

    public void close() {
        if (iamClient != null) {
            iamClient.close();
        }
    }
}
