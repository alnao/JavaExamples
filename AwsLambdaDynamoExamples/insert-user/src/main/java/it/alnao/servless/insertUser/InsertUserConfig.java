package it.alnao.servless.insertUser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

@Configuration
public class InsertUserConfig {
	
	@Value("${sicurezza.key}")
	private String key;
	@Value("${sicurezza.secret}")
	private String secret;
	
	@Bean
	public DynamoDBMapper dynamoDBMapper() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(
					new AWSStaticCredentialsProvider(new BasicAWSCredentials(key,secret))
				).withRegion(Regions.US_EAST_2)
				.build();
		return new DynamoDBMapper(client,DynamoDBMapperConfig.DEFAULT);
	}
}
