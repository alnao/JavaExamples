package com.xantrix.servless.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

@Configuration
public class DynamoDBConfig
{
	// Credenziali dalla default provider chain: in locale le variabili AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY
	// del .env nella root del repository, su AWS Lambda il ruolo di esecuzione della funzione.
	 @Value("${aws.region}")
	 private String region;
     
     @Bean
	 public DynamoDBMapper dynamoDBMapper() 
	 {
		 AmazonDynamoDB client = AmazonDynamoDBClientBuilder
				 .standard()
				 .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
	                .withRegion(region) 
	                .build();
		 
		 return new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT);
			 
	 }
}
