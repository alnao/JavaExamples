package it.alnao.cdk02istanzeEc2;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.DefaultStackSynthesizer;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

//import java.util.Arrays;

public class Cdk02IstanzeEc2App {
    public static void main(final String[] args) {
        App app = new App();

        // Tag standard applicati a tutte le risorse dello stack (vedi AWS/README.md#convenzione-dei-tag)
        // Il tag Name e' impostato su ogni singola risorsa nello stack (aws-j-cdkXX-<servizio>)
        Tags.of(app).add("Environment", "dev");
        Tags.of(app).add("Project", "Development.aws.j-CDK.CDK02istanzeEC2");
        Tags.of(app).add("Owner", "AlNao");
        Tags.of(app).add("CostCenter", "dev");
        Tags.of(app).add("ManagedBy", "CDK");

        // Bootstrap dedicato agli esempi Java (stack CDKToolkitJava), vedi AWS/CDK/README.md
        // env esplicito (account/regione della CLI) necessario per il lookup della VPC di default
        StackProps props = StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                .synthesizer(DefaultStackSynthesizer.Builder.create()
                        .qualifier("alnaojava")
                        .fileAssetsBucketName("alnao-dev-cdk-java")
                        .imageAssetsRepositoryName("alnao-dev-cdk-java")
                        .bootstrapStackVersionSsmParameter("/cdk-bootstrap-java/alnao/dev/version")
                        .build())
                .build();
        new Cdk02IstanzeEc2Stack(app, "Cdk02IstanzeEc2Stack", props);

        app.synth();
    }
}

