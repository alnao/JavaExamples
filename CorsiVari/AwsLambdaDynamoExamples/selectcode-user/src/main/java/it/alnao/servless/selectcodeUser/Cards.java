package it.alnao.servless.selectcodeUser;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument //per sottodocumento di Clienti, no tabella separata
public class Cards implements Serializable {
	private static final long serialVersionUID = 731426089588933520L;

	@DynamoDBAttribute(attributeName = "bollini")
	private int bollini;
	
	@DynamoDBAttribute(attributeName = "ultimaSpesa")
	private String ultimaSpesa;
}
