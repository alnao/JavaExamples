package it.alnao.aws.managerfx.controller;

import it.alnao.aws.managerfx.model.DefaultTags;
import it.alnao.aws.managerfx.model.TagIndex;
import it.alnao.aws.managerfx.service.AwsResourceService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.model.RestApi;
import software.amazon.awssdk.services.cloudformation.model.StackSummary;
import software.amazon.awssdk.services.cloudfront.model.DistributionSummary;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.eventbridge.model.Rule;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.sfn.model.StateMachineListItem;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.ssm.model.ParameterMetadata;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller per la finestra principale dell'applicazione
 * Gestisce l'interfaccia utente e le interazioni con i servizi AWS
 * 
 * @author AlNao
 * @version 1.0
 */
public class MainController {

    @FXML private ComboBox<String> regionComboBox;
    @FXML private ComboBox<String> profileComboBox;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;
    
    // Menu e pannelli
    @FXML private ListView<String> serviceListView;
    @FXML private javafx.scene.layout.StackPane contentPane;
    
    // Pannelli per ciascun servizio
    @FXML private javafx.scene.layout.VBox vpcPanel;
    @FXML private javafx.scene.layout.VBox ec2Panel;
    @FXML private javafx.scene.layout.VBox s3Panel;
    @FXML private javafx.scene.layout.VBox rdsPanel;
    @FXML private javafx.scene.layout.VBox sgPanel;
    @FXML private javafx.scene.layout.VBox cfnPanel;
    @FXML private javafx.scene.layout.VBox lambdaPanel;
    @FXML private javafx.scene.layout.VBox cloudFrontPanel;
    @FXML private javafx.scene.layout.VBox dynamoPanel;
    @FXML private javafx.scene.layout.VBox eventBridgePanel;
    @FXML private javafx.scene.layout.VBox snsPanel;
    @FXML private javafx.scene.layout.VBox sqsPanel;
    @FXML private javafx.scene.layout.VBox ssmPanel;
    @FXML private javafx.scene.layout.VBox stepFunctionsPanel;
    @FXML private javafx.scene.layout.VBox apiGatewayPanel;
    @FXML private javafx.scene.layout.VBox iamPanel;
    
    // Tab VPC
    @FXML private TextArea vpcInfoTextArea;
    @FXML private TableView<Subnet> subnetsTable;
    @FXML private TableColumn<Subnet, String> subnetIdColumn;
    @FXML private TableColumn<Subnet, String> subnetCidrColumn;
    @FXML private TableColumn<Subnet, String> subnetAzColumn;
    
    // Tab EC2
    @FXML private TableView<Instance> ec2Table;
    @FXML private TableColumn<Instance, String> ec2IdColumn;
    @FXML private TableColumn<Instance, String> ec2NameColumn;
    @FXML private TableColumn<Instance, String> ec2TypeColumn;
    @FXML private TableColumn<Instance, String> ec2StateColumn;
    @FXML private TableColumn<Instance, String> ec2IpColumn;
    
    // Tab S3
    @FXML private TableView<Bucket> s3Table;
    @FXML private TableColumn<Bucket, String> s3NameColumn;
    @FXML private TableColumn<Bucket, String> s3CreationColumn;
    
    // Tab RDS
    @FXML private TableView<DBInstance> rdsTable;
    @FXML private TableColumn<DBInstance, String> rdsIdColumn;
    @FXML private TableColumn<DBInstance, String> rdsEngineColumn;
    @FXML private TableColumn<DBInstance, String> rdsStatusColumn;
    @FXML private TableColumn<DBInstance, String> rdsEndpointColumn;
    
    // Tab Security Groups
    @FXML private TableView<SecurityGroup> sgTable;
    @FXML private TableColumn<SecurityGroup, String> sgIdColumn;
    @FXML private TableColumn<SecurityGroup, String> sgNameColumn;
    @FXML private TableColumn<SecurityGroup, String> sgDescriptionColumn;
    
    // Tab CloudFormation
    @FXML private TableView<software.amazon.awssdk.services.cloudformation.model.StackSummary> cfnTable;
    @FXML private TableColumn<software.amazon.awssdk.services.cloudformation.model.StackSummary, String> cfnNameColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.cloudformation.model.StackSummary, String> cfnStatusColumn;
    
    // Tab Lambda
    @FXML private TableView<software.amazon.awssdk.services.lambda.model.FunctionConfiguration> lambdaTable;
    @FXML private TableColumn<software.amazon.awssdk.services.lambda.model.FunctionConfiguration, String> lambdaNameColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.lambda.model.FunctionConfiguration, String> lambdaRuntimeColumn;
    
    // Tab CloudFront
    @FXML private TableView<software.amazon.awssdk.services.cloudfront.model.DistributionSummary> cloudFrontTable;
    @FXML private TableColumn<software.amazon.awssdk.services.cloudfront.model.DistributionSummary, String> cloudFrontIdColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.cloudfront.model.DistributionSummary, String> cloudFrontDomainColumn;
    
    // Tab DynamoDB
    @FXML private TableView<String> dynamoTable;
    @FXML private TableColumn<String, String> dynamoNameColumn;
    
    // Tab EventBridge
    @FXML private TableView<software.amazon.awssdk.services.eventbridge.model.Rule> eventBridgeTable;
    @FXML private TableColumn<software.amazon.awssdk.services.eventbridge.model.Rule, String> eventBridgeNameColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.eventbridge.model.Rule, String> eventBridgeStateColumn;
    
    // Tab SNS
    @FXML private TableView<software.amazon.awssdk.services.sns.model.Topic> snsTable;
    @FXML private TableColumn<software.amazon.awssdk.services.sns.model.Topic, String> snsArnColumn;
    
    // Tab SQS
    @FXML private TableView<String> sqsTable;
    @FXML private TableColumn<String, String> sqsUrlColumn;
    
    // Tab SSM
    @FXML private TableView<software.amazon.awssdk.services.ssm.model.ParameterMetadata> ssmTable;
    @FXML private TableColumn<software.amazon.awssdk.services.ssm.model.ParameterMetadata, String> ssmNameColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.ssm.model.ParameterMetadata, String> ssmTypeColumn;
    
    // Tab Step Functions
    @FXML private TableView<software.amazon.awssdk.services.sfn.model.StateMachineListItem> stepFunctionsTable;
    @FXML private TableColumn<software.amazon.awssdk.services.sfn.model.StateMachineListItem, String> stepFunctionsNameColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.sfn.model.StateMachineListItem, String> stepFunctionsArnColumn;
    
    // Tab API Gateway
    @FXML private TableView<software.amazon.awssdk.services.apigateway.model.RestApi> apiGatewayTable;
    @FXML private TableColumn<software.amazon.awssdk.services.apigateway.model.RestApi, String> apiGatewayIdColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.apigateway.model.RestApi, String> apiGatewayNameColumn;
    
    // Tab IAM
    @FXML private TableView<software.amazon.awssdk.services.iam.model.Group> iamGroupsTable;
    @FXML private TableColumn<software.amazon.awssdk.services.iam.model.Group, String> iamGroupNameColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.iam.model.Group, String> iamGroupArnColumn;
    
    @FXML private TableView<software.amazon.awssdk.services.iam.model.User> iamUsersTable;
    @FXML private TableColumn<software.amazon.awssdk.services.iam.model.User, String> iamUserNameColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.iam.model.User, String> iamUserArnColumn;
    
    @FXML private TableView<software.amazon.awssdk.services.iam.model.Role> iamRolesTable;
    @FXML private TableColumn<software.amazon.awssdk.services.iam.model.Role, String> iamRoleNameColumn;
    @FXML private TableColumn<software.amazon.awssdk.services.iam.model.Role, String> iamRoleArnColumn;
    
    // Colonne dei tag standard: valori per riga salvati nelle properties di ogni TableView
    private static final String TAGS_PROPERTY = "defaultTags";
    private static final String TAG_ERROR = "\u0000error";
    private static final String TAG_UNSUPPORTED = "\u0000unsupported";

    // Indice dei tag della regione, ricaricato a ogni refresh (Resource Groups Tagging API)
    private volatile TagIndex tagIndex = new TagIndex();

    private AwsResourceService awsService;
    private ObservableList<String> availableRegions;
    private ObservableList<String> availableProfiles;
    
    // Mappa per associare nome leggibile a region ID
    private Map<String, String> regionMap;

    /**
     * Inizializza il controller
     */
    @FXML
    public void initialize() {
        // Inizializza il servizio AWS
        awsService = new AwsResourceService();
        
        // Inizializza la mappa delle regioni con nomi in italiano
        initializeRegionMap();
        
        // Carica le regioni disponibili con nomi leggibili
        availableRegions = FXCollections.observableArrayList(regionMap.keySet());
        regionComboBox.setItems(availableRegions);
        regionComboBox.setValue("Irlanda (eu-west-1)");
        
        // Carica i profili disponibili (simulato - in produzione leggere da ~/.aws/credentials)
        availableProfiles = FXCollections.observableArrayList("default");
        profileComboBox.setItems(availableProfiles);
        profileComboBox.setValue("default");
        
        // Configura le colonne delle tabelle
        setupTableColumns();
        
        // Inizializza il menu laterale
        setupServiceMenu();
        
        // Aggiungi listener per la selezione regione/profilo
        regionComboBox.setOnAction(e -> updateAwsConfiguration());
        profileComboBox.setOnAction(e -> updateAwsConfiguration());
        
        // Carica i dati iniziali
        loadAwsResources();
    }
    
    /**
     * Inizializza il menu laterale dei servizi
     */
    private void setupServiceMenu() {
        ObservableList<String> services = FXCollections.observableArrayList(
            "VPC", 
            "EC2", 
            "S3", 
            "RDS", 
            "Security Groups",
            "CloudFormation",
            "Lambda",
            "CloudFront",
            "DynamoDB",
            "EventBridge",
            "SNS",
            "SQS",
            "SSM Parameters",
            "Step Functions",
            "API Gateway",
            "IAM"
        );
        
        serviceListView.setItems(services);
        serviceListView.getSelectionModel().selectFirst();
        
        // Listener per la selezione del servizio
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
        // Nascondi tutti i pannelli
        vpcPanel.setVisible(false);
        ec2Panel.setVisible(false);
        s3Panel.setVisible(false);
        rdsPanel.setVisible(false);
        sgPanel.setVisible(false);
        cfnPanel.setVisible(false);
        lambdaPanel.setVisible(false);
        cloudFrontPanel.setVisible(false);
        dynamoPanel.setVisible(false);
        eventBridgePanel.setVisible(false);
        snsPanel.setVisible(false);
        sqsPanel.setVisible(false);
        ssmPanel.setVisible(false);
        stepFunctionsPanel.setVisible(false);
        apiGatewayPanel.setVisible(false);
        iamPanel.setVisible(false);
        
        // Mostra il pannello selezionato
        switch (serviceName) {
            case "VPC": vpcPanel.setVisible(true); break;
            case "EC2": ec2Panel.setVisible(true); break;
            case "S3": s3Panel.setVisible(true); break;
            case "RDS": rdsPanel.setVisible(true); break;
            case "Security Groups": sgPanel.setVisible(true); break;
            case "CloudFormation": cfnPanel.setVisible(true); break;
            case "Lambda": lambdaPanel.setVisible(true); break;
            case "CloudFront": cloudFrontPanel.setVisible(true); break;
            case "DynamoDB": dynamoPanel.setVisible(true); break;
            case "EventBridge": eventBridgePanel.setVisible(true); break;
            case "SNS": snsPanel.setVisible(true); break;
            case "SQS": sqsPanel.setVisible(true); break;
            case "SSM Parameters": ssmPanel.setVisible(true); break;
            case "Step Functions": stepFunctionsPanel.setVisible(true); break;
            case "API Gateway": apiGatewayPanel.setVisible(true); break;
            case "IAM": iamPanel.setVisible(true); break;
        }
    }
    
    /**
     * Inizializza la mappa delle regioni con nomi leggibili in italiano
     * Ordine: Irlanda, Francoforte, poi altre in ordine alfabetico
     */
    private void initializeRegionMap() {
        regionMap = new LinkedHashMap<>();
        
        // Prima: Irlanda
        regionMap.put("Irlanda (eu-west-1)", "eu-west-1");
        
        // Seconda: Francoforte
        regionMap.put("Francoforte (eu-central-1)", "eu-central-1");
        
        // Altre regioni europee in ordine alfabetico
        regionMap.put("Londra (eu-west-2)", "eu-west-2");
        regionMap.put("Milano (eu-south-1)", "eu-south-1");
        regionMap.put("Parigi (eu-west-3)", "eu-west-3");
        regionMap.put("Stoccolma (eu-north-1)", "eu-north-1");
        regionMap.put("Zurigo (eu-central-2)", "eu-central-2");
        
        // Regioni USA in ordine alfabetico
        regionMap.put("California Nord (us-west-1)", "us-west-1");
        regionMap.put("Ohio (us-east-2)", "us-east-2");
        regionMap.put("Oregon (us-west-2)", "us-west-2");
        regionMap.put("Virginia Nord (us-east-1)", "us-east-1");
        
        // Regioni Asia Pacific in ordine alfabetico
        regionMap.put("Mumbai (ap-south-1)", "ap-south-1");
        regionMap.put("Osaka (ap-northeast-3)", "ap-northeast-3");
        regionMap.put("Seoul (ap-northeast-2)", "ap-northeast-2");
        regionMap.put("Singapore (ap-southeast-1)", "ap-southeast-1");
        regionMap.put("Sydney (ap-southeast-2)", "ap-southeast-2");
        regionMap.put("Tokyo (ap-northeast-1)", "ap-northeast-1");
        
        // Altre regioni in ordine alfabetico
        regionMap.put("Bahrein (me-south-1)", "me-south-1");
        regionMap.put("Canada (ca-central-1)", "ca-central-1");
        regionMap.put("Cape Town (af-south-1)", "af-south-1");
        regionMap.put("Hong Kong (ap-east-1)", "ap-east-1");
        regionMap.put("San Paolo (sa-east-1)", "sa-east-1");
    }

    /**
     * Configura le colonne delle tabelle
     */
    private void setupTableColumns() {
        // Subnets
        subnetIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().subnetId()));
        subnetCidrColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().cidrBlock()));
        subnetAzColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().availabilityZone()));
        
        // EC2
        ec2IdColumn.setCellValueFactory(new PropertyValueFactory<>("instanceId"));
        ec2NameColumn.setCellValueFactory(cellData -> {
            Instance instance = cellData.getValue();
            String name = instance.tags().stream()
                .filter(tag -> "Name".equals(tag.key()))
                .map(Tag::value)
                .findFirst()
                .orElse("N/A");
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        ec2TypeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().instanceType().toString()));
        ec2StateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().state().name().toString()));
        ec2IpColumn.setCellValueFactory(new PropertyValueFactory<>("publicIpAddress"));
        
        // S3
        s3NameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().name()));
        s3CreationColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().creationDate() != null ? cellData.getValue().creationDate().toString() : "N/A"));
        
        // RDS
        rdsIdColumn.setCellValueFactory(new PropertyValueFactory<>("dbInstanceIdentifier"));
        rdsEngineColumn.setCellValueFactory(new PropertyValueFactory<>("engine"));
        rdsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("dbInstanceStatus"));
        rdsEndpointColumn.setCellValueFactory(cellData -> {
            DBInstance instance = cellData.getValue();
            String endpoint = instance.endpoint() != null ? instance.endpoint().address() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(endpoint);
        });
        
        // Security Groups
        sgIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().groupId()));
        sgNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().groupName()));
        sgDescriptionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().description()));
        
        // CloudFormation
        cfnNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().stackName()));
        cfnStatusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().stackStatus().toString()));
        
        // Lambda
        lambdaNameColumn.setCellValueFactory(new PropertyValueFactory<>("functionName"));
        lambdaRuntimeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().runtime().toString()));
        
        // CloudFront
        cloudFrontIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().id()));
        cloudFrontDomainColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().domainName()));
        
        // DynamoDB
        dynamoNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
        
        // EventBridge
        eventBridgeNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().name()));
        eventBridgeStateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().state().toString()));
        
        // SNS
        snsArnColumn.setCellValueFactory(new PropertyValueFactory<>("topicArn"));
        
        // SQS
        sqsUrlColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
        
        // SSM
        ssmNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ssmTypeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().type().toString()));
        
        // Step Functions
        stepFunctionsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stepFunctionsArnColumn.setCellValueFactory(new PropertyValueFactory<>("stateMachineArn"));
        
        // API Gateway
        apiGatewayIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        apiGatewayNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // IAM
        iamGroupNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().groupName()));
        iamGroupArnColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().arn()));
        
        iamUserNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().userName()));
        iamUserArnColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().arn()));
        
        iamRoleNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().roleName()));
        iamRoleArnColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().arn()));

        // Colonne dei tag standard su tutte le tabelle
        addTagColumns(subnetsTable);
        addTagColumns(ec2Table);
        addTagColumns(s3Table);
        addTagColumns(rdsTable);
        addTagColumns(sgTable);
        addTagColumns(cfnTable);
        addTagColumns(lambdaTable);
        addTagColumns(cloudFrontTable);
        addTagColumns(dynamoTable);
        addTagColumns(eventBridgeTable);
        addTagColumns(snsTable);
        addTagColumns(sqsTable);
        addTagColumns(ssmTable);
        addTagColumns(stepFunctionsTable);
        addTagColumns(apiGatewayTable);
        addTagColumns(iamGroupsTable);
        addTagColumns(iamUsersTable);
        addTagColumns(iamRolesTable);
    }

    /**
     * Aggiunge alla tabella una colonna per ogni tag standard ({@link DefaultTags#KEYS}):
     * valore del tag se presente, icona di avviso se manca.
     */
    private <T> void addTagColumns(TableView<T> table) {
        for (String key : DefaultTags.KEYS) {
            TableColumn<T, String> column = new TableColumn<>(key);
            column.setPrefWidth("Project".equals(key) || "Name".equals(key) ? 200 : 110);
            column.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(tagValue(table, cellData.getValue(), key)));
            column.setCellFactory(c -> new TagCell<>(key));
            table.getColumns().add(column);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> String tagValue(TableView<T> table, T item, String key) {
        Map<T, Map<String, String>> allTags = (Map<T, Map<String, String>>) table.getProperties().get(TAGS_PROPERTY);
        Map<String, String> tags = allTags == null ? null : allTags.get(item);
        if (tags == null || tags == DefaultTags.ERROR) {
            return TAG_ERROR;
        }
        if (tags == DefaultTags.UNSUPPORTED) {
            return TAG_UNSUPPORTED;
        }
        String value = tags.get(key);
        return value == null || value.isBlank() ? null : value;
    }

    /** Calcola i tag di ogni riga (da chiamare nel thread di background: può fare chiamate AWS) */
    private <T> Map<T, Map<String, String>> collectTags(List<T> items, Function<T, Map<String, String>> tagsOf, boolean parallel) {
        return (parallel ? items.parallelStream() : items.stream())
            .collect(Collectors.toMap(Function.identity(), tagsOf, (a, b) -> a));
    }

    /** Mostra righe e relativi tag nella tabella (da chiamare nel thread JavaFX) */
    private <T> void showItems(TableView<T> table, List<T> items, Map<T, Map<String, String>> tags) {
        table.getProperties().put(TAGS_PROPERTY, tags);
        table.setItems(FXCollections.observableArrayList(items));
        table.refresh();
    }

    /** Cella di un tag standard: valore, icona di avviso se manca, "n/d" se non supportato, "?" se non leggibile */
    private static class TagCell<T> extends TableCell<T, String> {
        private final String key;

        TagCell(String key) {
            this.key = key;
        }

        @Override
        protected void updateItem(String value, boolean empty) {
            super.updateItem(value, empty);
            getStyleClass().removeAll("tag-missing", "tag-error", "tag-unsupported");
            setText(null);
            setGraphic(null);
            setTooltip(null);
            if (empty) {
                return;
            }
            if (TAG_ERROR.equals(value)) {
                setText("?");
                getStyleClass().add("tag-error");
                setTooltip(new Tooltip("Impossibile leggere i tag della risorsa (permessi o errore di rete)"));
            } else if (TAG_UNSUPPORTED.equals(value)) {
                setText("n/d");
                getStyleClass().add("tag-unsupported");
                setTooltip(new Tooltip("Questo tipo di risorsa non supporta i tag"));
            } else if (value == null) {
                Label icon = new Label("\u26A0");
                icon.getStyleClass().add("tag-missing-icon");
                setGraphic(icon);
                getStyleClass().add("tag-missing");
                setTooltip(new Tooltip("Tag " + key + " mancante"));
            } else {
                setText(value);
                setTooltip(new Tooltip(key + " = " + value));
            }
        }
    }

    /**
     * Aggiorna la configurazione AWS quando cambia regione o profilo
     */
    private void updateAwsConfiguration() {
        String regionDisplay = regionComboBox.getValue();
        String profile = profileComboBox.getValue();
        
        if (regionDisplay != null && profile != null) {
            String regionId = regionMap.get(regionDisplay);
            if (regionId != null) {
                awsService.updateConfiguration(Region.of(regionId), profile);
                statusLabel.setText("Configurazione aggiornata: " + regionDisplay + " - " + profile);
            }
        }
    }

    /**
     * Metodo pubblico per il bottone Refresh
     * Carica tutte le risorse AWS
     */
    @FXML
    public void loadResources() {
        loadAwsResources();
    }
    
    private void loadAwsResources() {
        statusLabel.setText("Caricamento risorse in corso...");
        refreshButton.setDisable(true);
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    tagIndex = awsService.getRegionalTagIndex();
                    loadVpcInfo();
                    loadEc2Instances();
                    loadS3Buckets();
                    loadRdsInstances();
                    loadSecurityGroups();
                    loadCloudFormationStacks();
                    loadLambdaFunctions();
                    loadCloudFrontDistributions();
                    loadDynamoDbTables();
                    loadEventBridgeRules();
                    loadSnsTopics();
                    loadSqsQueues();
                    loadSsmParameters();
                    loadStepFunctions();
                    loadApiGatewayApis();
                    loadIamResources();
                    
                    Platform.runLater(() -> {
                        statusLabel.setText(tagIndex.isAvailable()
                            ? "Risorse caricate con successo"
                            : "Risorse caricate, tag non disponibili: " + tagIndex.getError());
                        refreshButton.setDisable(false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Errore: " + e.getMessage());
                        refreshButton.setDisable(false);
                        showErrorAlert("Errore di caricamento", e.getMessage());
                    });
                }
                return null;
            }
        };
        
        new Thread(task).start();
    }

    /**
     * Carica le informazioni sulla VPC
     */
    private void loadVpcInfo() {
        try {
            Vpc defaultVpc = awsService.getDefaultVpc();
            List<Subnet> subnets = defaultVpc != null ? awsService.getSubnets(defaultVpc.vpcId()) : List.of();
            Map<Subnet, Map<String, String>> subnetTags = collectTags(subnets,
                s -> DefaultTags.toMap(s.tags(), t -> t.key(), t -> t.value()), false);
            
            Platform.runLater(() -> {
                if (defaultVpc != null) {
                    StringBuilder info = new StringBuilder();
                    info.append("VPC ID: ").append(defaultVpc.vpcId()).append("\n");
                    info.append("CIDR Block: ").append(defaultVpc.cidrBlock()).append("\n");
                    info.append("State: ").append(defaultVpc.state()).append("\n");
                    info.append("\nTag standard:\n");
                    Map<String, String> vpcTags = DefaultTags.toMap(defaultVpc.tags(), t -> t.key(), t -> t.value());
                    for (String key : DefaultTags.KEYS) {
                        String value = vpcTags.get(key);
                        info.append("  ").append(key).append(": ")
                            .append(value == null || value.isBlank() ? "\u26A0 mancante" : value).append("\n");
                    }
                    vpcInfoTextArea.setText(info.toString());
                    
                    showItems(subnetsTable, subnets, subnetTags);
                } else {
                    vpcInfoTextArea.setText("Nessuna VPC di default trovata");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le istanze EC2
     */
    private void loadEc2Instances() {
        try {
            List<Instance> instances = awsService.getEc2Instances();
            Map<Instance, Map<String, String>> tags = collectTags(instances, i -> DefaultTags.toMap(i.tags(), t -> t.key(), t -> t.value()), false);
            Platform.runLater(() -> showItems(ec2Table, instances, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica i bucket S3
     */
    private void loadS3Buckets() {
        try {
            List<Bucket> buckets = awsService.getS3Buckets();
            Map<Bucket, Map<String, String>> tags = collectTags(buckets, b -> awsService.getS3BucketTags(b.name()), true);
            Platform.runLater(() -> showItems(s3Table, buckets, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le istanze RDS
     */
    private void loadRdsInstances() {
        try {
            List<DBInstance> instances = awsService.getRdsInstances();
            Map<DBInstance, Map<String, String>> tags = collectTags(instances, d -> DefaultTags.toMap(d.tagList(), t -> t.key(), t -> t.value()), false);
            Platform.runLater(() -> showItems(rdsTable, instances, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica i Security Groups
     */
    private void loadSecurityGroups() {
        try {
            List<SecurityGroup> securityGroups = awsService.getSecurityGroups();
            Map<SecurityGroup, Map<String, String>> tags = collectTags(securityGroups, sg -> DefaultTags.toMap(sg.tags(), t -> t.key(), t -> t.value()), false);
            Platform.runLater(() -> showItems(sgTable, securityGroups, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica gli stack CloudFormation
     */
    private void loadCloudFormationStacks() {
        try {
            List<StackSummary> stacks = awsService.getCloudFormationStacks();
            Map<StackSummary, Map<String, String>> tags = collectTags(stacks, s -> tagIndex.byArn(s.stackId()), false);
            Platform.runLater(() -> showItems(cfnTable, stacks, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le funzioni Lambda
     */
    private void loadLambdaFunctions() {
        try {
            List<FunctionConfiguration> functions = awsService.getLambdaFunctions();
            Map<FunctionConfiguration, Map<String, String>> tags = collectTags(functions, f -> tagIndex.byArn(f.functionArn()), false);
            Platform.runLater(() -> showItems(lambdaTable, functions, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le distribuzioni CloudFront
     */
    private void loadCloudFrontDistributions() {
        try {
            List<DistributionSummary> distributions = awsService.getCloudFrontDistributions();
            Map<DistributionSummary, Map<String, String>> tags = collectTags(distributions, d -> awsService.getCloudFrontDistributionTags(d.arn()), true);
            Platform.runLater(() -> showItems(cloudFrontTable, distributions, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le tabelle DynamoDB
     */
    private void loadDynamoDbTables() {
        try {
            List<String> tables = awsService.getDynamoDbTables();
            Map<String, Map<String, String>> tags = collectTags(tables, name -> tagIndex.byResource("dynamodb", "table/" + name), false);
            Platform.runLater(() -> showItems(dynamoTable, tables, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le regole EventBridge
     */
    private void loadEventBridgeRules() {
        try {
            List<Rule> rules = awsService.getEventBridgeRules();
            Map<Rule, Map<String, String>> tags = collectTags(rules, r -> tagIndex.byArn(r.arn()), false);
            Platform.runLater(() -> showItems(eventBridgeTable, rules, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica i topic SNS
     */
    private void loadSnsTopics() {
        try {
            List<Topic> topics = awsService.getSnsTopics();
            Map<Topic, Map<String, String>> tags = collectTags(topics, tp -> tagIndex.byArn(tp.topicArn()), false);
            Platform.runLater(() -> showItems(snsTable, topics, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le code SQS
     */
    private void loadSqsQueues() {
        try {
            List<String> queues = awsService.getSqsQueues();
            Map<String, Map<String, String>> tags = collectTags(queues, url -> tagIndex.byResource("sqs", url.substring(url.lastIndexOf('/') + 1)), false);
            Platform.runLater(() -> showItems(sqsTable, queues, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica i parametri SSM
     */
    private void loadSsmParameters() {
        try {
            List<ParameterMetadata> parameters = awsService.getSsmParameters();
            Map<ParameterMetadata, Map<String, String>> tags = collectTags(parameters, p -> tagIndex.byResource("ssm", p.name().startsWith("/") ? "parameter" + p.name() : "parameter/" + p.name()), false);
            Platform.runLater(() -> showItems(ssmTable, parameters, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le Step Functions
     */
    private void loadStepFunctions() {
        try {
            List<StateMachineListItem> stateMachines = awsService.getStepFunctions();
            Map<StateMachineListItem, Map<String, String>> tags = collectTags(stateMachines, sm -> tagIndex.byArn(sm.stateMachineArn()), false);
            Platform.runLater(() -> showItems(stepFunctionsTable, stateMachines, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le API Gateway
     */
    private void loadApiGatewayApis() {
        try {
            List<RestApi> apis = awsService.getApiGatewayApis();
            Map<RestApi, Map<String, String>> tags = collectTags(apis, api -> tagIndex.byResource("apigateway", "/restapis/" + api.id()), false);
            Platform.runLater(() -> showItems(apiGatewayTable, apis, tags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica le risorse IAM (Groups, Users, Roles)
     */
    private void loadIamResources() {
        try {
            // i gruppi IAM non supportano i tag
            List<software.amazon.awssdk.services.iam.model.Group> groups = awsService.getIamGroups();
            Map<software.amazon.awssdk.services.iam.model.Group, Map<String, String>> groupTags =
                collectTags(groups, g -> DefaultTags.UNSUPPORTED, false);
            Platform.runLater(() -> showItems(iamGroupsTable, groups, groupTags));
            
            // ListUsers/ListRoles non restituiscono i tag: una chiamata per utente/ruolo, in parallelo
            List<software.amazon.awssdk.services.iam.model.User> users = awsService.getIamUsers();
            Map<software.amazon.awssdk.services.iam.model.User, Map<String, String>> userTags =
                collectTags(users, u -> awsService.getIamUserTags(u.userName()), true);
            Platform.runLater(() -> showItems(iamUsersTable, users, userTags));
            
            List<software.amazon.awssdk.services.iam.model.Role> roles = awsService.getIamRoles();
            Map<software.amazon.awssdk.services.iam.model.Role, Map<String, String>> roleTags =
                collectTags(roles, r -> awsService.getIamRoleTags(r.roleName()), true);
            Platform.runLater(() -> showItems(iamRolesTable, roles, roleTags));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra un alert di errore
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handler per il pulsante Refresh
     */
    @FXML
    private void handleRefresh() {
        loadAwsResources();
    }
}
