package it.alnao.awssdkexamples;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String... args) {
        logger.info("Application starts");

        //Handler handler = new Handler();
        //handler.sendRequest();

        if (args.length != 1) {
            System.out.println("001CreateBucket no parameter");
            return;
        }

        String bucketName = args[0];
        System.out.format("Creating a bucket named %s\n", bucketName);
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.EU_WEST_1;//eu-west-1
        
        S3Client s3 = S3Client.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build();

        createBucket (s3, bucketName);
        s3.close();

        logger.info("Application ends");
    }
    public static void createBucket( S3Client s3Client, String bucketName) {

        try {
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder().bucket(bucketName).build();
            s3Client.createBucket(bucketRequest);

            // Wait until the bucket is created and print out the response.
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder().bucket(bucketName).build();
            S3Waiter s3Waiter = s3Client.waiter();
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            //waiterResponse.matched().response().ifPresent(System.out::println);

            logger.info(bucketName +" is ready");

        } catch (S3Exception e) {
            System.err.println("Errore: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
    public static void removedBucket( S3Client s3Client, String bucketName) {
        try {
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
            s3Client.deleteBucket(deleteBucketRequest);
            
            // Wait until the bucket is created and print out the response.
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder().bucket(bucketName).build();
            S3Waiter s3Waiter = s3Client.waiter();
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketNotExists(bucketRequestWait);
            
            logger.info(bucketName +" removed");
        } catch (S3Exception e) {
        	System.err.println("Errore: " + e.awsErrorDetails().errorMessage());
        	throw e;
        }
    }
    public static boolean checkIfBucketExist( S3Client s3Client, String bucketName) {
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
        boolean exist=false;
        Iterator<Bucket> it=listBucketsResponse.buckets().iterator();
        while(it.hasNext()) {
        	Bucket b=it.next();
        	if (b.name().equals(bucketName))
        		exist=true;
        }		
        logger.info(bucketName +" checkIfBucketExist " + (exist ? "YES" : "NO" ) );
        return exist;
    }
    
}
