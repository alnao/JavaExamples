package it.alnao.javaexamples.awssdk.aws.ec2;

import it.alnao.javaexamples.awssdk.config.Ec2ClientFactory;
import it.alnao.javaexamples.awssdk.utils.RegionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KeyPairManager {

    private final Ec2ClientFactory clientFactory;

    private Ec2Client getEc2Client(String region){
        return clientFactory.getClient(RegionUtils.getRegionOrDefault( region) );
    }

    public List<KeyPairInfo> listKeyPairs() {
        Ec2Client ec2Client = getEc2Client( null );
        return ec2Client.describeKeyPairs().keyPairs();
    }
}
