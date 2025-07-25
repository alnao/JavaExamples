package it.alnao.javaexamples.awssdk.config;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Component
public class Ec2ClientFactory {

    public Ec2Client getClient(Region region) {
        return Ec2Client.builder()
                .region(region) //Region.of(regionName)
                .build();
    }
}
