package it.alnao.awssdkexamples;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

@TestMethodOrder(OrderAnnotation.class)
public class HandlerTest {
    private static S3Client s3;
    private static String bucketName = "bucket-createbysdkjava-formazione-";
    
    @BeforeAll
    public static void setUp() throws IOException {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        s3 = S3Client.builder()
                .region(Region.EU_WEST_1)
                .credentialsProvider(credentialsProvider)
                .build();
        LocalDateTime now = LocalDateTime.now();
        bucketName += now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.ENGLISH));
    }
//test main empty e ok
    @Test
    @Order(1)
    public void mainEmpty() {
    	String[] a=new String[0];
        App.main(a);
        boolean result=App.checkIfBucketExist(s3,bucketName);
        assertFalse(result);
        System.out.println("Test 1 main");
    }
    @Test
    @Order(2)
    public void mainWithBucketName() {
    	String[] ar2=new String[1];
    	ar2[0]=bucketName;
        App.main(ar2);
        boolean result=App.checkIfBucketExist(s3,bucketName);
        assertTrue(result);
    	App.removedBucket(s3,bucketName);
        result=App.checkIfBucketExist(s3,bucketName);
        assertFalse(result);
        System.out.println("Test 2 main");
    }
//test create
    @Test
    @Order(3)
    public void createBucket() {
    	System.out.println("Test 3 createBucket started");
        App.createBucket(s3,bucketName);
        boolean result=App.checkIfBucketExist(s3,bucketName);
        assertTrue(result);
        System.out.println("Test 3 createBucket");
    }
//test remove
    @Test
    @Order(4)
    public void removedBucket() {
    	System.out.println("Test 4 removedBucket started");
    	App.removedBucket(s3,bucketName);
        boolean result=App.checkIfBucketExist(s3,bucketName);
        assertFalse(result);
        System.out.println("Test 4 removedBucket");
    }/**/
    
//test createBucketWithEx with exc
    @Test
    @Order(5)
    public void createBucketWithEx() {
    	System.out.println("Test 5 createBucketWithEx started");
        //App.createBucket(s3,bucketName);
        assertThrows(Exception.class,  () -> {
        	App.createBucket(s3,bucketName+"@");
        } );
        System.out.println("Test 5 createBucketWithEx");
    }
//test removedBucket with exc
    @Test
    @Order(6)
    public void removeBucketWithEx() {
    	System.out.println("Test 6 removeBucketWithEx started");
        //App.createBucket(s3,bucketName);
        assertThrows(Exception.class,  () -> {
        	App.removedBucket(s3,bucketName+"@");
        } );
        System.out.println("Test 6 removeBucketWithEx");
    }
}
