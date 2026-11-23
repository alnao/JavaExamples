package it.alnao.aws.managerfx.service.impl;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;

import it.alnao.aws.managerfx.model.DefaultTags;

import java.util.List;
import java.util.Map;

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

    /**
     * Tag di un utente IAM (permesso IAM richiesto: iam:ListUserTags).
     * ListUsers non restituisce i tag, quindi serve una chiamata per utente.
     */
    public Map<String, String> getUserTags(String userName) {
        try {
            return DefaultTags.toMap(iamClient.listUserTags(r -> r.userName(userName)).tags(), Tag::key, Tag::value);
        } catch (Exception e) {
            System.err.println("[IamService] tag non leggibili per l'utente " + userName + ": " + e.getMessage());
            return DefaultTags.ERROR;
        }
    }

    /**
     * Tag di un ruolo IAM (permesso IAM richiesto: iam:ListRoleTags).
     * ListRoles non restituisce i tag, quindi serve una chiamata per ruolo.
     */
    public Map<String, String> getRoleTags(String roleName) {
        try {
            return DefaultTags.toMap(iamClient.listRoleTags(r -> r.roleName(roleName)).tags(), Tag::key, Tag::value);
        } catch (Exception e) {
            System.err.println("[IamService] tag non leggibili per il ruolo " + roleName + ": " + e.getMessage());
            return DefaultTags.ERROR;
        }
    }

    public void close() {
        if (iamClient != null) {
            iamClient.close();
        }
    }
}
