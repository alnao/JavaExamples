package it.alnao.cdk02istanzeEc2;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.CfnOutput;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStream;

/**
 * Istanza EC2 con web server Apache nella VPC di default dell'account/regione.
 * Richiede un env esplicito (account/regione) nello stack per il lookup della VPC di default.
 * Tipo di istanza da context: cdk deploy -c instanceType=t2.small (default t2.micro).
 */
public class Cdk02IstanzeEc2Stack extends Stack {
    public Cdk02IstanzeEc2Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public Cdk02IstanzeEc2Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Parametri CloudFormation (valori risolti al deploy)
        CfnParameter keyNameParam = CfnParameter.Builder.create(this, "KeyName")
                .type("AWS::EC2::KeyPair::KeyName")
                .description("Name of an existing EC2 KeyPair to enable SSH access to the instance")
                .build();

        CfnParameter allowedCidrParam = CfnParameter.Builder.create(this, "AllowedCIDR")
                .type("String")
                .defaultValue("0.0.0.0/0")
                .description("The IP address range that can be used to access the EC2 instance")
                .build();

        // Tipo di istanza da context: deve essere un valore concreto al synth (non un CfnParameter)
        String instanceType = (String) java.util.Optional.ofNullable(this.getNode().tryGetContext("instanceType")).orElse("t2.micro");

        // VPC di default dell'account/regione (lookup al synth, risultato salvato in cdk.context.json)
        IVpc vpc = Vpc.fromLookup(this, "VPC", VpcLookupOptions.builder()
                .isDefault(true)
                .build());

        // Create the security group
        SecurityGroup instanceSecurityGroup = SecurityGroup.Builder.create(this, "InstanceSecurityGroup")
                .vpc(vpc)
                .description("Enable SSH and HTTP access")
                .allowAllOutbound(true)
                .build();
        Tags.of(instanceSecurityGroup).add("Name", "aws-j-cdk02-sg");

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

        // Create the EC2 instance in a public subnet of the default VPC
        @SuppressWarnings("deprecation")
        Instance ec2Instance = Instance.Builder.create(this, "EC2Instance")
                .instanceType(parseInstanceType(instanceType))
                .machineImage(MachineImage.latestAmazonLinux2())
                .securityGroup(instanceSecurityGroup)
                .keyName(keyNameParam.getValueAsString())
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PUBLIC).build())
                .userData(UserData.custom(userData))
                .build();
        Tags.of(ec2Instance).add("Name", "aws-j-cdk02-ec2");
        Tags.of(ec2Instance.getRole()).add("Name", "aws-j-cdk02-role");

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
