package com.xantrix.servless.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.xantrix.servless.dao.ClienteCrudDao;
import com.xantrix.servless.model.Cards;
import com.xantrix.servless.model.Cliente;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;
   
@Component
@Slf4j
public class DataFunction implements Function<S3Event,String>
{
	@Autowired
	private ClienteCrudDao clienteRepository;
	
	@Value("${buckets.spese}")
	private String bucketSpese;
	 
    @Value("${buckets.backup}")
 	private String bucketBuckup;

	@Override
	public String apply(S3Event s3event) 
	{
		//Regions clientRegion = Regions.DEFAULT_REGION;
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
		
		S3EventNotificationRecord record = s3event.getRecords().get(0);
		
		String srcBucket = record.getS3().getBucket().getName();
		String srcKey = record.getS3().getObject().getUrlDecodedKey();
		
		log.info("Trovato il file " + srcKey + " nel Bucket " + srcBucket); 
		 
		
		S3Object s3Object = s3Client.getObject(new GetObjectRequest(
				srcBucket, srcKey));
		
		InputStream objectData = s3Object.getObjectContent();
		
		try 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
			
			String line;
			
			while((line = reader.readLine()) != null) 
			{
				log.info(line);
				String[] Items  = line.split(";");
				
				this.UpdateBollini(Items[0], Items[1]);
				
			}
			
			reader.close();
			
			String desKey = srcKey + "(" +  this.getCurrentDate() + ")";
			
			CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketSpese, srcKey, bucketBuckup, desKey);
			s3Client.copyObject(copyObjRequest);
			
			s3Client.deleteObject(bucketSpese, srcKey);
			
		} 
		catch (AmazonServiceException e) 
		{
			log.info("Errore: " + e.getErrorMessage()); 
			e.printStackTrace();
	    } 
		catch (SdkClientException e) 
		{
			log.info("Errore: " + e.getLocalizedMessage()); 
	        e.printStackTrace();
	    }
		catch (FileNotFoundException e) 
		{
			log.info("Errore: File Non Presente nel Bucket");
		}
		catch (IOException ex) 
		{
			 
			//ex.printStackTrace();
			log.info("Errore: " + ex.getMessage());
			
		}
		 
		return "Fatto";
	}
	
	private void UpdateBollini(String CodFid, String Bollini)
	{
		int bollini;
		
		try 
		{
			bollini = Integer.parseInt(Bollini);
			
		}
		catch (NumberFormatException e)
		{
			bollini = 0;
		}
		
		if (bollini != 0)
		{
			log.info("****** Cerchiamo l'Utente " + CodFid +  "*******");
			
			Cliente cliente =  clienteRepository.SelClienteByCode(CodFid);
			
			if (cliente != null)
			{
				log.info("****** Saldo Iniziale Monte Bollini " + cliente.getCards().getBollini() +  "*******");
				
				Cards card = cliente.getCards();
				
				int monteBollini = card.getBollini();
				
				monteBollini += bollini;
				
				if (monteBollini >= 0)
				{
					log.info("****** Saldo Finale Monte Bollini " + monteBollini +  "*******");
					
					 
					String strDate = this.getCurrentDate();
					
					card.setBollini(monteBollini);
					card.setUltimaspesa(strDate);
					
					cliente.setCards(card);
					
					clienteRepository.insCliente(cliente);
					
					log.info("****** Saldo Bollini Cliente " + CodFid +  " Modificato *******");
					
				}
			}
			else
			{
				log.info("****** Cliente " + CodFid +  " Non trovato! *******");
			}
		}
	}
	
	private String getCurrentDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
}
