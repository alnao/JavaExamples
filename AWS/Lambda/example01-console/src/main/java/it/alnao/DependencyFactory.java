package it.alnao;

//import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
//import software.amazon.awssdk.core.SdkSystemSetting;
//import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.example01-console.$serviceClientPrefixClient;

/**
 * The module containing all dependencies required by the {@link App}.
 */
public class DependencyFactory {

    private DependencyFactory() {}

    /**
     * @return an instance of $serviceClientPrefixClient
     *//*
    public static $serviceClientPrefixClient ${serviceClientVariable}Client() {
        return $serviceClientPrefixClient.builder()
                       .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                       .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                       .httpClientBuilder(UrlConnectionHttpClient.builder())
                       .build();
    } */
}
