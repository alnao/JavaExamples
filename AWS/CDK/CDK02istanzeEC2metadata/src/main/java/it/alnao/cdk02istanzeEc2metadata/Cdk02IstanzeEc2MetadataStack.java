package it.alnao.cdk02istanzeEc2metadata;

import software.constructs.Construct;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.CfnWaitCondition;
import software.amazon.awscdk.CfnWaitConditionHandle;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;

import java.util.Arrays;
import java.util.Map;


public class Cdk02IstanzeEc2MetadataStack extends Stack {
    public Cdk02IstanzeEc2MetadataStack(final Construct scope, final String id) {
        this(scope, id, null);
        
    }
    
    public Cdk02IstanzeEc2MetadataStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        Cdk02IstanzeEc2MetadataUtils utils=new Cdk02IstanzeEc2MetadataUtils();

        // Parameters
        CfnParameter subnetId = CfnParameter.Builder.create(this, "SubnetId")
                .type("AWS::EC2::Subnet::Id")
                .description("Please choose a Subnet Id")
                .build();

        CfnParameter vpcId = CfnParameter.Builder.create(this, "VpcId")
                .type("AWS::EC2::VPC::Id")
                .description("Id of Vpc")
                .build();

        CfnParameter keyName = CfnParameter.Builder.create(this, "KeyName")
                .type("AWS::EC2::KeyPair::KeyName")
                .description("Name of an existing EC2 KeyPair to enable SSH access to the instance")
                .build();

        CfnParameter instanceType = CfnParameter.Builder.create(this, "InstanceType")
                .type("String")
                .description("WebServer EC2 instance type")
                .defaultValue("t2.micro")
                .allowedValues(Arrays.asList("t2.nano", "t2.micro", "t2.small"))
                .build();

        CfnParameter sshLocation = CfnParameter.Builder.create(this, "SSHLocation")
                .type("String")
                .description("The IP address range that can be used to SSH to the EC2 instances")
                .defaultValue("0.0.0.0/0")
                .allowedPattern("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})/(\\d{1,2})")
                .build();

        // VPC
        IVpc vpc = Vpc.fromLookup(this, "VPC", VpcLookupOptions.builder()
                .vpcId(vpcId.getValueAsString())
                .build());

        // Security Group
        SecurityGroup securityGroup = SecurityGroup.Builder.create(this, "InstanceSecurityGroup")
                .vpc(vpc)  // Usa la variabile vpc già definita
                .description("Enable SSH and HTTP access")
                .allowAllOutbound(true)
                .build();

        securityGroup.addIngressRule(Peer.ipv4(sshLocation.getValueAsString()), Port.tcp(22), "SSH access");
        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "HTTP access");

        // IAM Role for EC2
        Role ec2Role = Role.Builder.create(this, "EC2Role")
                .assumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .managedPolicies(Arrays.asList(
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonSSMManagedInstanceCore"),
                        ManagedPolicy.fromAwsManagedPolicyName("CloudWatchAgentServerPolicy")
                ))
                .build();

        // Wait Condition Handle - DEVE essere definito prima di essere usato nel User Data
        CfnWaitConditionHandle waitConditionHandle = CfnWaitConditionHandle.Builder.create(this, "SampleWaitConditionHandle")
                .build();

        // User Data
        /*
        UserData userData = UserData.forLinux();
        userData.addCommands(
                "#!/bin/bash -xe",
                "echo \"TEST VpcId=" + vpc.getVpcId() + " SubnetId=" + subnetId.getValueAsString() + " stack=" + this.getStackName() + " region=" + this.getRegion() + "\" > /tmp/test.txt",
                "yum update -y aws-cfn-bootstrap",
                "/opt/aws/bin/cfn-init -v --stack " + this.getStackName() + " --resource EC2Instance --region " + this.getRegion(),
                "INIT_STATUS=$?",
                // Usa l'URL del wait condition handle invece del nome della risorsa
                "/opt/aws/bin/cfn-signal -e $INIT_STATUS '" + waitConditionHandle.getRef() + "'",
                "exit $INIT_STATUS"
        );*/
        String userDataScript = utils.loadUserDataFromFile(
                vpc.getVpcId(), 
                subnetId.getValueAsString(), 
                this.getStackName(), 
                this.getRegion(), 
                waitConditionHandle.getRef()
        );
        UserData userData = UserData.custom(userDataScript);

        // EC2 Instance
        InstanceType ec2InstanceType = utils.parseInstanceType(instanceType.getValueAsString());

        @SuppressWarnings("deprecation")
        Instance ec2Instance = Instance.Builder.create(this, "EC2Instance")
                .instanceType(ec2InstanceType)
                .machineImage(MachineImage.latestAmazonLinux2())
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder().subnets(Arrays.asList(Subnet.fromSubnetId(this, "ImportedSubnet", subnetId.getValueAsString()))).build())
                .securityGroup(securityGroup)
                .keyName(keyName.getValueAsString())
                .userData(userData)
                .role(ec2Role)
                .build();

        // Metadata
        // Sostituisci la parte dei metadata con:
        Map<String, Object> metadata = utils.loadMetadataFromFile();
        /*
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("AWS::CloudFormation::Init", Collections.singletonMap("config", new HashMap<String, Object>() {{
            put("packages", Collections.singletonMap("yum", Collections.singletonMap("httpd", Collections.emptyList())));
            put("files", Collections.singletonMap("/var/www/html/index.html", new HashMap<String, Object>() {{
                put("content", "<h1>Hello World from EC2 instance!</h1><p>This was created using cfn-init</p>");
                put("mode", "000644");
            }}));
            put("commands", new HashMap<String, Object>() {{
                put("01_echo", new HashMap<String, Object>() {{
                    put("command", "echo 'commando1 ok' > comando1.html");
                    put("cwd", "/var/www/html/");
                }});
                put("02_echo", new HashMap<String, Object>() {{
                    put("command", "echo 'commando2 ok' > /var/www/html/comando2.html");
                }});
            }});
            put("services", Collections.singletonMap("sysvinit", Collections.singletonMap("httpd", new HashMap<String, Object>() {{
                put("enabled", "true");
                put("ensureRunning", "true");
            }})));
        }}));*/

        CfnInstance cfnInstance = (CfnInstance) ec2Instance.getNode().getDefaultChild();
        cfnInstance.addMetadata("AWS::CloudFormation::Init", metadata.get("AWS::CloudFormation::Init"));

        // Wait Condition - aspetta che l'istanza segnali il completamento
        CfnWaitCondition waitCondition = CfnWaitCondition.Builder.create(this, "SampleWaitCondition")
                .handle(waitConditionHandle.getRef())
                .timeout("PT15M")
                .count(1)
                .build();

        // Aggiungi dipendenza esplicita (opzionale ma consigliato)
        waitCondition.getNode().addDependency(ec2Instance);
                
        // Outputs
        new CfnOutput(this, "InstanceIdOutput", CfnOutputProps.builder()
                .value(ec2Instance.getInstanceId())
                .exportName("InstanceId")
                .build());
        new CfnOutput(this, "AZOutput", CfnOutputProps.builder()
                .value(ec2Instance.getInstanceAvailabilityZone())
                .exportName("AZ")
                .build());
        new CfnOutput(this, "PublicDNSOutput", CfnOutputProps.builder()
                .value(ec2Instance.getInstancePublicDnsName())
                .exportName("PublicDNS")
                .build());
        new CfnOutput(this, "PublicIPOutput", CfnOutputProps.builder()
                .value(ec2Instance.getInstancePublicIp())
                .exportName("PublicIP")
                .build());
        new CfnOutput(this, "StackNameOutput", CfnOutputProps.builder()
                .value(this.getStackName())
                .exportName("StackName")
                .build());
    }

}