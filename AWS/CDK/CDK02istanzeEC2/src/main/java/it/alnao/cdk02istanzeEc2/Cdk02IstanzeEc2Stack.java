package it.alnao.cdk02istanzeEc2;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.CfnOutput;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStream;

public class Cdk02IstanzeEc2Stack extends Stack {
    public Cdk02IstanzeEc2Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public Cdk02IstanzeEc2Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Define parameters
        CfnParameter instanceTypeParam = CfnParameter.Builder.create(this, "InstanceType")
                .type("String")
                .defaultValue("t2.micro")
                .allowedValues(java.util.Arrays.asList("t2.micro", "t2.small", "t2.medium"))
                .description("Enter t2.micro, t2.small, or t2.medium. Default is t2.micro.")
                .build();

        CfnParameter keyNameParam = CfnParameter.Builder.create(this, "KeyName")
                .type("AWS::EC2::KeyPair::KeyName")
                .description("Name of an existing EC2 KeyPair to enable SSH access to the instance")
                .build();

        CfnParameter allowedCidrParam = CfnParameter.Builder.create(this, "AllowedCIDR")
                .type("String")
                .defaultValue("0.0.0.0/0")
                .description("The IP address range that can be used to access the EC2 instance")
                .build();

        CfnParameter vpcIdParam = CfnParameter.Builder.create(this, "VpcId")
                .type("AWS::EC2::VPC::Id")
                .description("VPC to launch the instance into")
                .build();

        CfnParameter amiIdParam = CfnParameter.Builder.create(this, "AmiId")
                .type("AWS::SSM::Parameter::Value<AWS::EC2::Image::Id>")
                .defaultValue("/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2")
                .description("AMI ID for the instance (default is latest Amazon Linux 2)")
                .build();

        CfnParameter regionAZParam = CfnParameter.Builder.create(this, "RegionAZ")
                .type("AWS::EC2::AvailabilityZone::Name")
                .defaultValue("eu-central-1a")
                .description("Availability Zone for the instance")
                .build();

        CfnParameter subnetIdParam = CfnParameter.Builder.create(this, "SubnetId")
                .type("AWS::EC2::Subnet::Id")
                .description("Subnet to launch the instance into")
                .build();

        IVpc vpc = Vpc.fromLookup(this, "VPC", VpcLookupOptions.builder()
                .vpcId(vpcIdParam.getValueAsString())
                .build());

        // Create the security group
        SecurityGroup instanceSecurityGroup = SecurityGroup.Builder.create(this, "InstanceSecurityGroup")
                .vpc(vpc)
                .description("Enable SSH and HTTP access")
                .allowAllOutbound(true)
                .build();

        instanceSecurityGroup.addIngressRule(Peer.ipv4(allowedCidrParam.getValueAsString()), Port.tcp(22), "Allow SSH access");
        instanceSecurityGroup.addIngressRule(Peer.ipv4(allowedCidrParam.getValueAsString()), Port.tcp(80), "Allow HTTP access");

        // Read user data script
        String userData;
        try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("user-data.sh");
                userData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
                throw new RuntimeException("Unable to read user data script", e);
        }

        // Create the EC2 instance
        @SuppressWarnings("deprecation")
        Instance ec2Instance = Instance.Builder.create(this, "EC2Instance")
                .instanceType(parseInstanceType(instanceTypeParam.getValueAsString()))
                .machineImage(MachineImage.fromSsmParameter(amiIdParam.getValueAsString()))
                .securityGroup(instanceSecurityGroup)
                .keyName(keyNameParam.getValueAsString())
                .vpc(vpc)
                .availabilityZone(regionAZParam.getValueAsString())
                .vpcSubnets(SubnetSelection.builder().subnets(java.util.Arrays.asList(Subnet.fromSubnetId(this, "Subnet", subnetIdParam.getValueAsString()))).build())
                .userData(UserData.custom(userData))
                .build();

        // Add outputs
        CfnOutput.Builder.create(this, "InstanceId")
                .description("InstanceId of the newly created EC2 instance")
                .value(ec2Instance.getInstanceId())
                .build();

        CfnOutput.Builder.create(this, "AZ")
                .description("Availability Zone of the newly created EC2 instance")
                .value(ec2Instance.getInstanceAvailabilityZone())
                .build();

        CfnOutput.Builder.create(this, "PublicDNS")
                .description("Public DNSName of the newly created EC2 instance")
                .value(ec2Instance.getInstancePublicDnsName())
                .build();

        CfnOutput.Builder.create(this, "PublicIP")
                .description("Public IP address of the newly created EC2 instance")
                .value(ec2Instance.getInstancePublicIp())
                .build();
    }

    private InstanceType parseInstanceType(String instanceType) {
        String[] parts = instanceType.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid instance type format: " + instanceType);
        }
        InstanceClass instanceClass = InstanceClass.valueOf(parts[0].toUpperCase());
        InstanceSize instanceSize = InstanceSize.valueOf(parts[1].toUpperCase());
        return InstanceType.of(instanceClass, instanceSize);
    }
}