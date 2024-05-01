package com.xantrix.servless.model;

import java.io.Serializable;
 

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@DynamoDBDocument
public class Cards implements Serializable
{ 
	private static final long serialVersionUID = -7509028795141019392L;
	
	@DynamoDBAttribute(attributeName = "bollini")
	private int bollini;
	
	@DynamoDBAttribute(attributeName = "ultimaspesa")
	private String ultimaspesa;

}
 