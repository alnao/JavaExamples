package com.xantrix.servless.config;

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
public class DynamoDBConfig
{
	 @Value("${sicurezza.key}")
	 private String Key;
	 
     @Value("${sicurezza.secret}")
 	 private String Secret;
     
     @Bean
	 public DynamoDBMapper dynamoDBMapper() 
	 {
		 AmazonDynamoDB client = AmazonDynamoDBClientBuilder
				 .standard()
				 .withCredentials(new AWSStaticCredentialsProvider(
	                		new BasicAWSCredentials(Key, Secret)))
	                .withRegion(Regions.EU_WEST_3) 
	                .build();
		 
		 return new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT);
			 
	 }
}
