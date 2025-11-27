package it.alnao.aws.managerfx.service;

import it.alnao.aws.managerfx.service.impl.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.model.StackSummary;
import software.amazon.awssdk.services.cloudfront.model.DistributionSummary;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.eventbridge.model.Rule;
import software.amazon.awssdk.services.iam.model.Group;
import software.amazon.awssdk.services.iam.model.Role;
import software.amazon.awssdk.services.iam.model.User;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.sfn.model.StateMachineListItem;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.ssm.model.ParameterMetadata;
import software.amazon.awssdk.services.apigateway.model.RestApi;

import java.util.List;

/**
 * Servizio centrale per l'interazione con le risorse AWS
 * Coordina tutti i servizi specifici per i vari prodotti AWS
 * 
 * @author AlNao
 * @version 2.0
 */
public class AwsResourceService {

    private Region region;
    private String profile;
    
    // Servizi specifici
    private Ec2Service ec2Service;
    private S3Service s3Service;
    private RdsService rdsService;
    private IamService iamService;
    private CloudFormationService cloudFormationService;
    private LambdaService lambdaService;
    private DynamoDbService dynamoDbService;
    private CloudFrontService cloudFrontService;
    private EventBridgeService eventBridgeService;
    private SnsService snsService;
    private SqsService sqsService;
    private SsmService ssmService;
    private StepFunctionsService stepFunctionsService;
    private ApiGatewayService apiGatewayService;

    public AwsResourceService() {
        this(Region.EU_CENTRAL_1, "default");
    }

    public AwsResourceService(Region region, String profile) {
        this.region = region;
        this.profile = profile;
        initializeServices();
    }

    private void initializeServices() {
        ec2Service = new Ec2Service(region, profile);
        s3Service = new S3Service(region, profile);
        rdsService = new RdsService(region, profile);
        iamService = new IamService(profile);
        cloudFormationService = new CloudFormationService(region, profile);
        lambdaService = new LambdaService(region, profile);
        dynamoDbService = new DynamoDbService(region, profile);
        cloudFrontService = new CloudFrontService(profile);
        eventBridgeService = new EventBridgeService(region, profile);
        snsService = new SnsService(region, profile);
        sqsService = new SqsService(region, profile);
        ssmService = new SsmService(region, profile);
        stepFunctionsService = new StepFunctionsService(region, profile);
        apiGatewayService = new ApiGatewayService(region, profile);
    }

    public void updateConfiguration(Region newRegion, String newProfile) {
        if (!newRegion.equals(this.region) || !newProfile.equals(this.profile)) {
            this.region = newRegion;
            this.profile = newProfile;
            closeServices();
            initializeServices();
        }
    }

    private void closeServices() {
        if (ec2Service != null) ec2Service.close();
        if (s3Service != null) s3Service.close();
        if (rdsService != null) rdsService.close();
        if (iamService != null) iamService.close();
        if (cloudFormationService != null) cloudFormationService.close();
        if (lambdaService != null) lambdaService.close();
        if (dynamoDbService != null) dynamoDbService.close();
        if (cloudFrontService != null) cloudFrontService.close();
        if (eventBridgeService != null) eventBridgeService.close();
        if (snsService != null) snsService.close();
        if (sqsService != null) sqsService.close();
        if (ssmService != null) ssmService.close();
        if (stepFunctionsService != null) stepFunctionsService.close();
        if (apiGatewayService != null) apiGatewayService.close();
    }

    // EC2 Methods
    public Vpc getDefaultVpc() {
        return ec2Service.getDefaultVpc();
    }

    public List<Subnet> getSubnets(String vpcId) {
        return ec2Service.getSubnets(vpcId);
    }

    public List<Instance> getEc2Instances() {
        return ec2Service.getInstances();
    }

    public List<SecurityGroup> getSecurityGroups() {
        return ec2Service.getSecurityGroups();
    }

    // S3 Methods
    public List<Bucket> getS3Buckets() {
        return s3Service.getBuckets();
    }

    // RDS Methods
    public List<DBInstance> getRdsInstances() {
        return rdsService.getInstances();
    }

    // IAM Methods
    public List<Group> getIamGroups() {
        return iamService.getGroups();
    }

    public List<User> getIamUsers() {
        return iamService.getUsers();
    }

    public List<Role> getIamRoles() {
        return iamService.getRoles();
    }

    // CloudFormation Methods
    public List<StackSummary> getCloudFormationStacks() {
        return cloudFormationService.getStacks();
    }

    // Lambda Methods
    public List<FunctionConfiguration> getLambdaFunctions() {
        return lambdaService.getFunctions();
    }

    // DynamoDB Methods
    public List<String> getDynamoDbTables() {
        return dynamoDbService.getTables();
    }

    // CloudFront Methods
    public List<DistributionSummary> getCloudFrontDistributions() {
        return cloudFrontService.getDistributions();
    }

    // EventBridge Methods
    public List<Rule> getEventBridgeRules() {
        return eventBridgeService.getRules();
    }

    // SNS Methods
    public List<Topic> getSnsTopics() {
        return snsService.getTopics();
    }

    // SQS Methods
    public List<String> getSqsQueues() {
        return sqsService.getQueues();
    }

    // SSM Methods
    public List<ParameterMetadata> getSsmParameters() {
        return ssmService.getParameters();
    }

    // Step Functions Methods
    public List<StateMachineListItem> getStepFunctions() {
        return stepFunctionsService.getStateMachines();
    }

    // API Gateway Methods
    public List<RestApi> getApiGatewayApis() {
        return apiGatewayService.getApis();
    }

    public void close() {
        closeServices();
    }
}
