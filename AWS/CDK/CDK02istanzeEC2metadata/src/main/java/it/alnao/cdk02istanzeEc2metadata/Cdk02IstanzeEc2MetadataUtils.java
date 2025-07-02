package it.alnao.cdk02istanzeEc2metadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;

public class Cdk02IstanzeEc2MetadataUtils {
    
    protected InstanceType parseInstanceType(String instanceType) {
        String[] parts = instanceType.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid instance type format: " + instanceType);
        }
        InstanceClass instanceClass = InstanceClass.valueOf(parts[0].toUpperCase());
        InstanceSize instanceSize = InstanceSize.valueOf(parts[1].toUpperCase());
        return InstanceType.of(instanceClass, instanceSize);
    }
    @SuppressWarnings("unchecked")
    protected  Map<String, Object> loadMetadataFromFile() {
        try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ec2-metadata.json");
                return mapper.readValue(inputStream, Map.class);
        } catch (Exception e) {
                throw new RuntimeException("Failed to load metadata from file", e);
        }
    }
/*
    //import org.yaml.snakeyaml.Yaml;
    protected static  Map<String, Object> loadMetadataFromYaml() {
        try {
                Yaml yaml = new Yaml();
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ec2-metadata.yaml");
                return yaml.load(inputStream);
        } catch (Exception e) {
                throw new RuntimeException("Failed to load metadata from YAML", e);
        }
    }
 */       
   protected String loadUserDataFromFile(String vpcId, String subnetId, String stackName, String region, String waitConditionHandle) {
        try {
                // Carica il file dallo classpath
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("user-data.sh");
                String userDataTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                
                // Sostituisci le variabili
                return userDataTemplate
                        .replace("${VPC_ID}", vpcId)
                        .replace("${SUBNET_ID}", subnetId)
                        .replace("${STACK_NAME}", stackName)
                        .replace("${REGION}", region)
                        .replace("${WAIT_CONDITION_HANDLE}", waitConditionHandle);
                        
        } catch (IOException e) {
                throw new RuntimeException("Failed to load user data from file", e);
        }
   } 
}
