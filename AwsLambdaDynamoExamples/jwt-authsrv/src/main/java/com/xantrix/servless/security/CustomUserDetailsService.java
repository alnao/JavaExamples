package com.xantrix.servless.security;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service("customUserDetailsService")
@Slf4j
public class CustomUserDetailsService implements UserDetailsService
{
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomUserDetailsService.class); //se @Slf4j non funzia
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
		
	@Override
	public UserDetails loadUserByUsername(String UserId) 
			throws UsernameNotFoundException
	{
		String ErrMsg = "";
		
		if (UserId == null || UserId.length() < 2) 
		{
			ErrMsg = "Nome utente assente o non valido";
			
			log.warn(ErrMsg);
			
	    	throw new UsernameNotFoundException(ErrMsg); 
		} 
		
		//TODO Ottenere i dati dell'utente da tabella DynamoDb
		Utenti utente = this.GetUser(UserId);
		
		if (utente == null)
		{
			ErrMsg = String.format("Utente %s non Trovato!!", UserId);
			
			log.warn(ErrMsg);
			
			throw new UsernameNotFoundException(ErrMsg);
		}
		
		UserBuilder builder = null;
		builder = org.springframework.security.core.userdetails.User.withUsername(utente.getUserId());
		builder.disabled((utente.getAttivo().equals("Si") ? false : true));
		builder.password(utente.getPassword());
		
		String[] profili = utente.getRuoli()
				 .stream().map(a -> "ROLE_" + a).toArray(String[]::new);
		
		builder.authorities(profili);
		
		return builder.build();
		
		
	}
	
	//METODO TEMPORANEO CON DATI HARD CODED
	private Utenti GetUser(String UserId)
	{
		if (UserId.equals("Alnao"))
		{
			List<String> ruoli =  new ArrayList<>();
			ruoli.add("USER");
			
			return new Utenti("AlNao", passwordEncoder.encode("bello"),"Si",ruoli);
		}
		else if (UserId.equals("Admin"))
		{
			List<String> ruoli =  new ArrayList<>();
			ruoli.add("USER");
			ruoli.add("ADMIN");
			
			return new Utenti("Admin", passwordEncoder.encode("Segreta"),"Si",ruoli);
		}
		else
			return null;
	}
	
	
	
	
	
	
	
	
	
}
	