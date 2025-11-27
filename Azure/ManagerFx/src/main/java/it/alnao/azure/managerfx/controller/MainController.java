package it.alnao.azure.managerfx.controller;

import it.alnao.azure.managerfx.service.AzureResourceService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
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

/**
 * Controller per la finestra principale dell'applicazione Azure Manager
 * 
 * @author AlNao
 * @version 1.0
 */
public class MainController {

    @FXML private ComboBox<String> subscriptionComboBox;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;
    
    // Menu e pannelli
    @FXML private ListView<String> serviceListView;
    @FXML private javafx.scene.layout.StackPane contentPane;
    
    // Pannelli per ciascun servizio
    @FXML private javafx.scene.layout.VBox resourceGroupsPanel;
    @FXML private javafx.scene.layout.VBox virtualMachinesPanel;
    @FXML private javafx.scene.layout.VBox virtualNetworksPanel;
    @FXML private javafx.scene.layout.VBox storageAccountsPanel;
    @FXML private javafx.scene.layout.VBox sqlServersPanel;
    @FXML private javafx.scene.layout.VBox webAppsPanel;
    @FXML private javafx.scene.layout.VBox functionAppsPanel;
    @FXML private javafx.scene.layout.VBox aksPanel;
    @FXML private javafx.scene.layout.VBox cosmosDbPanel;
    
    // Tabelle Resource Groups
    @FXML private TableView<ResourceGroup> resourceGroupsTable;
    @FXML private TableColumn<ResourceGroup, String> rgNameColumn;
    @FXML private TableColumn<ResourceGroup, String> rgLocationColumn;
    
    // Tabelle Virtual Machines
    @FXML private TableView<VirtualMachine> virtualMachinesTable;
    @FXML private TableColumn<VirtualMachine, String> vmNameColumn;
    @FXML private TableColumn<VirtualMachine, String> vmSizeColumn;
    @FXML private TableColumn<VirtualMachine, String> vmStatusColumn;
    @FXML private TableColumn<VirtualMachine, String> vmLocationColumn;
    
    // Tabelle Virtual Networks
    @FXML private TableView<Network> virtualNetworksTable;
    @FXML private TableColumn<Network, String> vnetNameColumn;
    @FXML private TableColumn<Network, String> vnetAddressColumn;
    @FXML private TableColumn<Network, String> vnetLocationColumn;
    
    // Tabelle Storage Accounts
    @FXML private TableView<StorageAccount> storageAccountsTable;
    @FXML private TableColumn<StorageAccount, String> storageNameColumn;
    @FXML private TableColumn<StorageAccount, String> storageLocationColumn;
    @FXML private TableColumn<StorageAccount, String> storageSkuColumn;
    
    // Tabelle SQL Servers
    @FXML private TableView<SqlServer> sqlServersTable;
    @FXML private TableColumn<SqlServer, String> sqlNameColumn;
    @FXML private TableColumn<SqlServer, String> sqlLocationColumn;
    @FXML private TableColumn<SqlServer, String> sqlVersionColumn;
    
    // Tabelle Web Apps
    @FXML private TableView<WebApp> webAppsTable;
    @FXML private TableColumn<WebApp, String> webAppNameColumn;
    @FXML private TableColumn<WebApp, String> webAppLocationColumn;
    @FXML private TableColumn<WebApp, String> webAppStateColumn;
    
    // Tabelle Function Apps
    @FXML private TableView<FunctionApp> functionAppsTable;
    @FXML private TableColumn<FunctionApp, String> functionAppNameColumn;
    @FXML private TableColumn<FunctionApp, String> functionAppLocationColumn;
    @FXML private TableColumn<FunctionApp, String> functionAppStateColumn;
    
    // Tabelle AKS
    @FXML private TableView<KubernetesCluster> aksTable;
    @FXML private TableColumn<KubernetesCluster, String> aksNameColumn;
    @FXML private TableColumn<KubernetesCluster, String> aksLocationColumn;
    @FXML private TableColumn<KubernetesCluster, String> aksVersionColumn;
    
    // Tabelle Cosmos DB
    @FXML private TableView<CosmosDBAccount> cosmosDbTable;
    @FXML private TableColumn<CosmosDBAccount, String> cosmosNameColumn;
    @FXML private TableColumn<CosmosDBAccount, String> cosmosLocationColumn;
    @FXML private TableColumn<CosmosDBAccount, String> cosmosTypeColumn;
    
    private AzureResourceService azureService;
    private ObservableList<String> availableSubscriptions;
    
    /**
     * Inizializzazione del controller
     */
    @FXML
    public void initialize() {
        azureService = new AzureResourceService();
        
        // Carica dinamicamente le subscription disponibili
        loadAvailableSubscriptions();
        
        // Configura le colonne delle tabelle
        setupTableColumns();
        
        // Inizializza il menu laterale
        setupServiceMenu();
        
        // Aggiungi listener per la selezione subscription
        subscriptionComboBox.setOnAction(e -> updateAzureConfiguration());
        
        // Carica i dati iniziali se c'è una subscription selezionata
        if (subscriptionComboBox.getValue() != null) {
            loadAzureResources();
        }
    }
    
    /**
     * Configura le colonne delle tabelle
     */
    private void setupTableColumns() {
        // Resource Groups
        rgNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        rgLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        
        // Virtual Machines
        vmNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        vmSizeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().size().toString()));
        vmStatusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().powerState().toString()));
        vmLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        
        // Virtual Networks
        vnetNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        vnetAddressColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().addressSpaces().toString()));
        vnetLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        
        // Storage Accounts
        storageNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        storageLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        storageSkuColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().skuType().name().toString()));
        
        // SQL Servers
        sqlNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        sqlLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        sqlVersionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().version()));
        
        // Web Apps
        webAppNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        webAppLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        webAppStateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().state()));
        
        // Function Apps
        functionAppNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        functionAppLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        functionAppStateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().state()));
        
        // AKS
        aksNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        aksLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        aksVersionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().version()));
        
        // Cosmos DB
        cosmosNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().name()));
        cosmosLocationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().regionName()));
        cosmosTypeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().kind().toString()));
    }
    
    /**
     * Inizializza il menu laterale dei servizi
     */
    private void setupServiceMenu() {
        ObservableList<String> services = FXCollections.observableArrayList(
            "Resource Groups",
            "Virtual Machines",
            "Virtual Networks",
            "Storage Accounts",
            "SQL Servers",
            "Web Apps",
            "Function Apps",
            "AKS",
            "Cosmos DB"
        );
        
        serviceListView.setItems(services);
        serviceListView.getSelectionModel().selectFirst();
        
        serviceListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showServicePanel(newVal);
            }
        });
    }
    
    /**
     * Mostra il pannello del servizio selezionato
     */
    private void showServicePanel(String serviceName) {
        resourceGroupsPanel.setVisible(false);
        virtualMachinesPanel.setVisible(false);
        virtualNetworksPanel.setVisible(false);
        storageAccountsPanel.setVisible(false);
        sqlServersPanel.setVisible(false);
        webAppsPanel.setVisible(false);
        functionAppsPanel.setVisible(false);
        aksPanel.setVisible(false);
        cosmosDbPanel.setVisible(false);
        
        switch (serviceName) {
            case "Resource Groups": resourceGroupsPanel.setVisible(true); break;
            case "Virtual Machines": virtualMachinesPanel.setVisible(true); break;
            case "Virtual Networks": virtualNetworksPanel.setVisible(true); break;
            case "Storage Accounts": storageAccountsPanel.setVisible(true); break;
            case "SQL Servers": sqlServersPanel.setVisible(true); break;
            case "Web Apps": webAppsPanel.setVisible(true); break;
            case "Function Apps": functionAppsPanel.setVisible(true); break;
            case "AKS": aksPanel.setVisible(true); break;
            case "Cosmos DB": cosmosDbPanel.setVisible(true); break;
        }
    }
    
    /**
     * Carica le subscription disponibili da Azure
     */
    private void loadAvailableSubscriptions() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() {
                updateMessage("Caricamento subscription...");
                return azureService.getAvailableSubscriptions();
            }
            
            @Override
            protected void succeeded() {
                List<String> subscriptions = getValue();
                availableSubscriptions = FXCollections.observableArrayList(subscriptions);
                subscriptionComboBox.setItems(availableSubscriptions);
                
                if (!availableSubscriptions.isEmpty()) {
                    // Non impostiamo il valore qui per evitare il binding del task loadAzureResources
                    Platform.runLater(() -> {
                        subscriptionComboBox.setValue(availableSubscriptions.get(0));
                        updateMessage("Subscription caricate: " + subscriptions.size());
                    });
                } else {
                    updateMessage("Nessuna subscription trovata. Verifica l'autenticazione Azure.");
                }
            }
            
            @Override
            protected void failed() {
                updateMessage("Errore caricamento subscription: " + getException().getMessage());
                // Fallback: subscription manuale
                Platform.runLater(() -> {
                    availableSubscriptions = FXCollections.observableArrayList(
                        "Inserisci Subscription ID"
                    );
                    subscriptionComboBox.setItems(availableSubscriptions);
                    subscriptionComboBox.setEditable(true);
                });
            }
        };
        
        statusLabel.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }
    
    /**
     * Aggiorna la configurazione Azure
     */
    private void updateAzureConfiguration() {
        String subscriptionId = subscriptionComboBox.getValue();
        if (subscriptionId != null && !subscriptionId.isEmpty() 
            && !subscriptionId.equals("Inserisci Subscription ID")) {
            azureService.updateConfiguration(subscriptionId);
            loadAzureResources();
        }
    }
    
    /**
     * Metodo pubblico per il bottone Refresh
     */
    @FXML
    public void loadResources() {
        loadAzureResources();
    }
    
    /**
     * Carica tutte le risorse Azure
     */
    private void loadAzureResources() {
        // Unbind eventuale binding precedente
        statusLabel.textProperty().unbind();
        statusLabel.setText("Caricamento risorse in corso...");
        refreshButton.setDisable(true);
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    loadResourceGroups();
                    loadVirtualMachines();
                    loadVirtualNetworks();
                    loadStorageAccounts();
                    loadSqlServers();
                    loadWebApps();
                    loadFunctionApps();
                    loadAks();
                    loadCosmosDb();
                    
                    Platform.runLater(() -> {
                        statusLabel.setText("Risorse caricate con successo");
                        refreshButton.setDisable(false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Errore nel caricamento: " + e.getMessage());
                        refreshButton.setDisable(false);
                    });
                    e.printStackTrace();
                }
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    private void loadResourceGroups() {
        List<ResourceGroup> rgs = azureService.getResourceGroups();
        Platform.runLater(() -> 
            resourceGroupsTable.setItems(FXCollections.observableArrayList(rgs)));
    }
    
    private void loadVirtualMachines() {
        List<VirtualMachine> vms = azureService.getVirtualMachines();
        Platform.runLater(() -> 
            virtualMachinesTable.setItems(FXCollections.observableArrayList(vms)));
    }
    
    private void loadVirtualNetworks() {
        List<Network> vnets = azureService.getVirtualNetworks();
        Platform.runLater(() -> 
            virtualNetworksTable.setItems(FXCollections.observableArrayList(vnets)));
    }
    
    private void loadStorageAccounts() {
        List<StorageAccount> storages = azureService.getStorageAccounts();
        Platform.runLater(() -> 
            storageAccountsTable.setItems(FXCollections.observableArrayList(storages)));
    }
    
    private void loadSqlServers() {
        List<SqlServer> sqls = azureService.getSqlServers();
        Platform.runLater(() -> 
            sqlServersTable.setItems(FXCollections.observableArrayList(sqls)));
    }
    
    private void loadWebApps() {
        List<WebApp> webApps = azureService.getWebApps();
        Platform.runLater(() -> 
            webAppsTable.setItems(FXCollections.observableArrayList(webApps)));
    }
    
    private void loadFunctionApps() {
        List<FunctionApp> functionApps = azureService.getFunctionApps();
        Platform.runLater(() -> 
            functionAppsTable.setItems(FXCollections.observableArrayList(functionApps)));
    }
    
    private void loadAks() {
        List<KubernetesCluster> aks = azureService.getKubernetesClusters();
        Platform.runLater(() -> 
            aksTable.setItems(FXCollections.observableArrayList(aks)));
    }
    
    private void loadCosmosDb() {
        List<CosmosDBAccount> cosmos = azureService.getCosmosDBAccounts();
        Platform.runLater(() -> 
            cosmosDbTable.setItems(FXCollections.observableArrayList(cosmos)));
    }
}
