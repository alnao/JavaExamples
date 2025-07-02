package it.alnao.cdk02istanzeEc2metadata;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class Cdk02IstanzeEc2MetadataApp {
    public static void main(final String[] args) {
        App app = new App();

        new Cdk02IstanzeEc2MetadataStack(app, "Cdk02IstanzeEc2MetadataStack", StackProps.builder().build());

        app.synth();
    }
}

