package it.alnao.awssdkexamples;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfilesConfigFile;

/**
 * see http://www.java2s.com/example/java-src/pkg/aws/profile-7503f.html
 * */
public class ProfilesUtils { 
	
	

	
	//see http://www.java2s.com/example/java-src/pkg/aws/profile-7503f.html
    public static ArrayList<String> getCustomerList(/*ProfilesConfigFile credentials*/) {
        File configfile = new File(System.getProperty("user.home"), ".aws/credentials");
        ProfilesConfigFile credentials = new ProfilesConfigFile(configfile);
        if (credentials == null) {
            throw new RuntimeException("No AWS security credentials found");
        }
        ArrayList<String> customers = new ArrayList<String>();
        for (String key : credentials.getAllProfiles().keySet()) {
            customers.add(key);
        }
        return customers;
    }
    /*public static String ProfileChoice(ArrayList customers) {
        //As you may have many profiles to chose from , let's let the user pick one
        System.out.println("Which profile do you want to use?");
        System.out.println(customers.toString());
        Scanner scan = new Scanner(System.in);
        String profile = scan.next();
        //setClientname(profile);
        return profile;
    }*/
    //http://www.java2s.com/example/java-api/com/amazonaws/auth/awscredentialsprovider/awscredentialsprovider-0-0.html
    public static AWSCredentialsProvider getAwsCredentialProvider(String accessId, String accessKey) {
        return new AWSCredentialsProvider() {
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials( accessId,  accessKey);
            }
            public void refresh() {
                // NOP
            }
        };
    }
}
