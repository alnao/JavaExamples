package it.alnao.awssdkexamples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class BucketUtils {
	  private static final Logger logger = LoggerFactory.getLogger(BucketUtils.class);
	   //https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/java/example_code/s3/src/main/java/aws/example/s3/ListBuckets.java
    public static List<Bucket> getBucketList(AmazonS3 s3){  
    	logger.info(".......... getBucketList");
    	List<Bucket> buckets = s3.listBuckets();
    	return buckets;
    	//System.out.println("Your {S3} buckets are:");
    	//for (Bucket b : buckets) {
    	//	System.out.println("* " + b.getName());
    	//}
    }
    
    //https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/java/example_code/s3/src/main/java/aws/example/s3/ListObjects.java
    public static List<S3ObjectSummary> getObjectList(AmazonS3 s3, String bucket_name, String path){
    	ListObjectsV2Request r=new ListObjectsV2Request().withBucketName(bucket_name).withMaxKeys(1000);
    	if (path==null) {
    		r.setDelimiter("/");
    		logger.info(".......... bucket_name "+ bucket_name + " senza path ");
    	} else {
    		if (path.endsWith("/")) {
    			path=path.substring(0,path.length()-1);
    		}
    		r.setPrefix(path);
    		logger.info(".......... bucket_name "+ bucket_name + " path " + path);
    	}
    	ListObjectsV2Result result = s3.listObjectsV2(r );// (bucket_name);
    	List<S3ObjectSummary> objects = result.getObjectSummaries();
    	
    	if (path==null) { //nella root devo aggiungere le cartelle ritornate qui
	    	List<String> commonPrefixes = result.getCommonPrefixes();
	    	for (String e :commonPrefixes) {
	    		//logger.info("..........e "+e);
	    		S3ObjectSummary s=new S3ObjectSummary();
	    		s.setKey(e);
	    		s.setBucketName(bucket_name);
	    		objects.add(s);
	    	}
    	}else {//se sottopath cerco altri 1000
    		for (int i=0;i<10 && result.isTruncated();i++) {//max 1000 * 10 for
        		logger.info(".......... getNextContinuationToken="+ result.getNextContinuationToken());
        		r.setContinuationToken( result.getNextContinuationToken() );
        		result = s3.listObjectsV2(r );// (bucket_name);
        		objects.addAll( result.getObjectSummaries() );
        	}
    		//rimuovo cartella sorgente
    		S3ObjectSummary oToRemove=null;
    		for (S3ObjectSummary o : objects) {
    			if (o.getKey().equals(path + "/"))
    				oToRemove=o;
    		}
    		objects.remove(oToRemove);
    		//rimuovo tutti i sotto file nelle sottocartelle
    		List<S3ObjectSummary> objectsr=new ArrayList<S3ObjectSummary>();
    		for (S3ObjectSummary o : objects) {
    			if (o.getKey().replace(path + "/","").indexOf("/") == -1) {
    				objectsr.add(o);//file nella cartella
    			}else
    			if (o.getKey().replace(path + "/","").indexOf("/") ==
    					o.getKey().replace(path + "/","").length()-1) {
    				objectsr.add(o);//file nella cartella	
    			}
    		}
    		objects=objectsr;
    	}
    	objects.sort((p1, p2) -> p1.getKey().compareTo(p2.getKey()) );
/*    			new Comparator<S3ObjectSummary>() {
            @Override
            public int compare(S3ObjectSummary p1, S3ObjectSummary p2) {
                return p1.getKey().compareTo(p2.getKey())
            }
        });//String.CASE_INSENSITIVE_ORDER);*/
    	return objects;
    }
    
    //https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-s3-objects.html
    public static boolean downloadFile(AmazonS3 s3, S3ObjectSummary ob, String dest_path) {
    	logger.info("*** downloadFile " + ob.getBucketName() + " /// " + ob.getKey()  );
    	if (ob.getKey().endsWith("/")) {
    		return false;
    	}
    	try {
    	    S3Object o = s3.getObject( ob.getBucketName() , ob.getKey() );
    	    S3ObjectInputStream s3is = o.getObjectContent();
    	    String file_name=ob.getKey();
    	    file_name = file_name.split("/")[ file_name.split("/").length-1 ];
    	    logger.info("*** file_name " + file_name  );
    	    FileOutputStream fos = new FileOutputStream(new File(dest_path + file_name));
    	    byte[] read_buf = new byte[1024];
    	    int read_len = 0;
    	    while ((read_len = s3is.read(read_buf)) > 0) {
    	        fos.write(read_buf, 0, read_len);
    	    }
    	    s3is.close();
    	    fos.close();
    	} catch (AmazonServiceException e) {
    	    System.err.println(e.getErrorMessage());
    	    logger.error( e.getErrorMessage() );
    	    return false;
    	} catch (FileNotFoundException e) {
    	    System.err.println(e.getMessage());
    	    logger.error( e.getMessage() );
    	    return false;
    	} catch (IOException e) {
    	    System.err.println(e.getMessage());
    	    logger.error( e.getMessage() );
    	    return false;
    	}
    	return true;
    }
    
    public static boolean copyFileFromS3ToS3(AmazonS3 s3Client, S3ObjectSummary ob, String to_bucket, String to_key) {
    	if (to_key==null) {
    		String file_name=ob.getKey();
    		to_key = file_name.split("/")[ file_name.split("/").length-1 ];
    	}
    	s3Client.copyObject( ob.getBucketName() , ob.getKey(), to_bucket, to_key);
    	return true;
    }
    
    //see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/java/example_code/s3/src/main/java/aws/example/s3/UploadObject.java
    public static boolean uploadFileFromLocalToS3(AmazonS3 s3Client, String to_bucket, String path, File file) throws Exception {
    	String to_key=path + file.getName();
        PutObjectRequest request = new PutObjectRequest(to_bucket, to_key, file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType( file.toURL().openConnection().getContentType() );
        //metadata.addUserMetadata("title", "someTitle");
        request.setMetadata(metadata);
        s3Client.putObject(request);
        return true;
    }

}
