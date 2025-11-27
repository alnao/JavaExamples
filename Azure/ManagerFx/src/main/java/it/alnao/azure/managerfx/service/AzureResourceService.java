package it.alnao.azure.managerfx.service;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.network.models.Network;
import com.azure.resourcemanager.storage.models.StorageAccount;
import com.azure.resourcemanager.sql.models.SqlServer;
import com.azure.resourcemanager.appservice.models.WebApp;
import com.azure.resourcemanager.appservice.models.FunctionApp;
import com.azure.resourcemanager.containerservice.models.KubernetesCluster;
import com.azure.resourcemanager.cosmos.models.CosmosDBAccount;
import com.azure.resourcemanager.resources.models.ResourceGroup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Servizio centrale per la gestione delle risorse Azure
 * 
 * @author AlNao
 * @version 1.0
 */
public class AzureResourceService {
    
    private AzureResourceManager azureResourceManager;
    private String currentSubscriptionId;
    
    /**
     * Costruttore
     */
    public AzureResourceService() {
        // Inizializzazione vuota, verrà configurato con updateConfiguration
    }
    
    /**
     * Ottiene la lista delle subscription disponibili
     */
    public List<String> getAvailableSubscriptions() {
        try {
            TokenCredential credential = new DefaultAzureCredentialBuilder().build();
            AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
            
            AzureResourceManager tempManager = AzureResourceManager
                .configure()
                .authenticate(credential, profile)
                .withDefaultSubscription();
            
            return StreamSupport.stream(
                tempManager.subscriptions().list().spliterator(), false)
                .map(subscription -> subscription.subscriptionId())
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            System.err.println("Errore nel recupero delle subscription: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Aggiorna la configurazione Azure con subscription
     */
    public void updateConfiguration(String subscriptionId) {
        try {
            this.currentSubscriptionId = subscriptionId;
            
            TokenCredential credential = new DefaultAzureCredentialBuilder().build();
            AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
            
            this.azureResourceManager = AzureResourceManager
                .configure()
                .authenticate(credential, profile)
                .withSubscription(subscriptionId);
                
            System.out.println("Connessione Azure stabilita per subscription: " + subscriptionId);
        } catch (Exception e) {
            System.err.println("Errore nella configurazione Azure: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Ottiene la lista dei Resource Groups
     */
    public List<ResourceGroup> getResourceGroups() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.resourceGroups().list().spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene la lista delle Virtual Machines
     */
    public List<VirtualMachine> getVirtualMachines() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.virtualMachines().list().spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene la lista delle Virtual Networks
     */
    public List<Network> getVirtualNetworks() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.networks().list().spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene la lista degli Storage Accounts
     */
    public List<StorageAccount> getStorageAccounts() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.storageAccounts().list().spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene la lista dei SQL Servers
     */
    public List<SqlServer> getSqlServers() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.sqlServers().list().spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene la lista delle Web Apps
     */
    public List<WebApp> getWebApps() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.webApps().list().spliterator(), false)
            .map(webAppBasic -> azureResourceManager.webApps().getById(webAppBasic.id()))
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene la lista delle Function Apps
     */
    public List<FunctionApp> getFunctionApps() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.functionApps().list().spliterator(), false)
            .map(functionAppBasic -> azureResourceManager.functionApps().getById(functionAppBasic.id()))
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene la lista dei Kubernetes Clusters (AKS)
     */
    public List<KubernetesCluster> getKubernetesClusters() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.kubernetesClusters().list().spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene la lista dei Cosmos DB Accounts
     */
    public List<CosmosDBAccount> getCosmosDBAccounts() {
        if (azureResourceManager == null) return List.of();
        return StreamSupport.stream(
            azureResourceManager.cosmosDBAccounts().list().spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * Chiude le connessioni
     */
    public void close() {
        // Cleanup se necessario
    }
    
    public String getCurrentSubscriptionId() {
        return currentSubscriptionId;
    }
}
