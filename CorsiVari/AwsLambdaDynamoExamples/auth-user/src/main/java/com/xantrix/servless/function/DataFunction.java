package com.xantrix.servless.function;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xantrix.servless.io.AuthPolicy;
import com.xantrix.servless.io.TokenAuthorizerContext;
import com.xantrix.servless.security.JwtTokenUtil;
import com.xantrix.servless.security.Utenti;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
  
@Component
@Slf4j
public class DataFunction implements Function<TokenAuthorizerContext,AuthPolicy>
{
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataFunction.class); //se @Slf4j non funzia
	/*
	@Autowired
	private ClienteCrudDao clienteRepository;
	*/
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public AuthPolicy apply(TokenAuthorizerContext input) 
	{
		String token = input.getAuthorizationToken();
		
		

    	// validate the incoming token
        // and produce the principal user identifier associated with the token

        // this could be accomplished in a number of ways:
        // 1. Call out to OAuth provider
        // 2. Decode a JWT token in-line
        // 3. Lookup in a self-managed DB
        String principalId = "aflaf78fd7afalnv";

        // if the client token is not recognized or invalid
        // you can send a 401 Unauthorized response to the client by failing like so:
        // throw new RuntimeException("Unauthorized");

        // if the token is valid, a policy should be generated which will allow or deny access to the client

        // if access is denied, the client will receive a 403 Access Denied response
        // if access is allowed, API Gateway will proceed with the back-end integration configured on the method that was called
        
        log.warn("Token: " + token);
		
		String username = null;
		String jwtToken = null;
		
		if (token != null && token.startsWith("Bearer ")) 
		{
			jwtToken = token.substring(7);
			
			try 
			{
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} 
			catch (IllegalArgumentException e) 
			{
				log.error("IMPOSSIBILE OTTENERE LA USERID", e);
				throw new RuntimeException("Errore: IMPOSSIBILE OTTENERE LA USERID");
			} 
			catch (ExpiredJwtException e) 
			{
				log.warn("TOKEN SCADUTO", e);
				throw new RuntimeException("Errore: TOKEN SCADUTO");
			}
		}
		else 
		{
			log.warn("TOKEN NON VALIDO");
			throw new RuntimeException("Errore: TOKEN NON VALIDO");
		}
		
		log.debug("JWT_TOKEN_USERNAME_VALUE '{}'", username);
		
		

    	String methodArn = input.getMethodArn();
    	String[] arnPartials = methodArn.split(":");
    	String region = arnPartials[3];
    	String awsAccountId = arnPartials[4];
    	
    	String[] apiGatewayArnPartials = arnPartials[5].split("/");
    	String restApiId = apiGatewayArnPartials[0];
    	String stage = apiGatewayArnPartials[1];
    	String httpMethod = apiGatewayArnPartials[2];
    	String resource = ""; // root resource
    	
    	if (apiGatewayArnPartials.length == 4) 
    	{
    		resource = apiGatewayArnPartials[3];
    	}

        // this function must generate a policy that is associated with the recognized principal user identifier.
        // depending on your use case, you might store policies in a DB, or generate them on the fly

        // keep in mind, the policy is cached for 5 minutes by default (TTL is configurable in the authorizer)
        // and will apply to subsequent calls to any method/resource in the RestApi
        // made with the same token

        // the example policy below denies access to all resources in the RestApi
    	
    	if (username != null)
		{
			//TODO Ottenere i dati dell'utente da tabella DynamoDb
			Utenti utente = GetUser(username);
			
			if (utente == null)
				throw new RuntimeException("Errore: UTENTE NON PRESENTE");
			
			if (utente.getAttivo().equals("Si"))
			{
				//La funzione verifica l'accesso agli end point destinati agli amministratori
				if (utente.getRuoli().contains("USER")) 
				{
					return new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getAllowAllPolicy(region, awsAccountId, restApiId, stage));
				}
				else
				{
					return new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getDenyAllPolicy(region, awsAccountId, restApiId, stage));
				}
			}
			else
			{
				throw new RuntimeException("Errore: UTENTE NON ATTIVO");
			}
			
			
		}
    	else
    	{
    		throw new RuntimeException("Errore: UTENTE NON ATTIVO");
    	}
    	
    	

	}
	
	//METODO TEMPORANEO CON DATI HARD CODED
	private Utenti GetUser(String UserId)
	{
		if (UserId.equals("AlNao"))
		{
			List<String> ruoli =  new ArrayList<>();
			ruoli.add("USER");
			
			return new Utenti("AlNao", "","Si",ruoli);
		}
		else if (UserId.equals("Admin"))
		{
			List<String> ruoli =  new ArrayList<>();
			ruoli.add("USER");
			ruoli.add("ADMIN");
			
			return new Utenti("Admin","","Si",ruoli);
		}
		else
			return null;
	}
}
