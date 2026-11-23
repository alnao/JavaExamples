package it.alnao.cdk01bucketS3;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.DefaultStackSynthesizer;
import software.amazon.awscdk.StackProps;
//import software.amazon.awscdk.Environment;
//import java.util.Arrays;

public class Cdk01BucketS3App {
    public static void main(final String[] args) {
        App app = new App();

        // Tag standard applicati a tutte le risorse dello stack (vedi AWS/README.md#convenzione-dei-tag)
        // Il tag Name e' impostato su ogni singola risorsa nello stack (aws-j-cdkXX-<servizio>)
        Tags.of(app).add("Environment", "dev");
        Tags.of(app).add("Project", "Development.aws.j-CDK.CDK01bucketS3");
        Tags.of(app).add("Owner", "AlNao");
        Tags.of(app).add("CostCenter", "dev");
        Tags.of(app).add("ManagedBy", "CDK");

        // Bootstrap dedicato agli esempi Java (stack CDKToolkitJava), vedi AWS/CDK/README.md
        StackProps props = StackProps.builder()
                .synthesizer(DefaultStackSynthesizer.Builder.create()
                        .qualifier("alnaojava")
                        .fileAssetsBucketName("alnao-dev-cdk-java")
                        .imageAssetsRepositoryName("alnao-dev-cdk-java")
                        .bootstrapStackVersionSsmParameter("/cdk-bootstrap-java/alnao/dev/version")
                        .build())
                .build();
        new Cdk01BucketS3Stack(app, "Cdk01BucketS3Stack", props);

        app.synth();
    }
                // If you don't specify 'env', this stack will be environment-agnostic.
                // Account/Region-dependent features and context lookups will not work,
                // but a single synthesized template can be deployed anywhere.

                // Uncomment the next block to specialize this stack for the AWS Account
                // and Region that are implied by the current CLI configuration.
                /*
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                */

                // Uncomment the next block if you know exactly what Account and Region you
                // want to deploy the stack to.
                /*
                .env(Environment.builder()
                        .account("123456789012")
                        .region("us-east-1")
                        .build())
                */

                // For more information, see https://docs.aws.amazon.com/cdk/latest/guide/environments.html

}

