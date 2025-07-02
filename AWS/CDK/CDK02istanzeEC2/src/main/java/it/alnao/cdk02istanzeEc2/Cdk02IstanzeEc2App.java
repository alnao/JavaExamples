package it.alnao.cdk02istanzeEc2;

import software.amazon.awscdk.App;
//import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

//import java.util.Arrays;

public class Cdk02IstanzeEc2App {
    public static void main(final String[] args) {
        App app = new App();

        new Cdk02IstanzeEc2Stack(app, "Cdk02IstanzeEc2Stack", StackProps.builder().build());

        app.synth();
    }
}

