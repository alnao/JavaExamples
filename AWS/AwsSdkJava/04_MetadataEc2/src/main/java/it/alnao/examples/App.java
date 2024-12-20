package it.alnao.examples;

import software.amazon.awssdk.imds.Ec2MetadataClient;
import software.amazon.awssdk.imds.Ec2MetadataResponse;

public class App {
    public static void main( String[] args ) {
        Ec2MetadataClient client = Ec2MetadataClient.create();
        Ec2MetadataResponse response = client.get("/latest/meta-data/ami-id");
        System.out.println(response.asString());
        client.close(); // Closes the internal resources used by the Ec2MetadataClient class.
    }
}
