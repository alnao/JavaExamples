package com.xantrix.servless.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.xantrix.servless.security.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

//import java.util.Objects;
import java.util.function.Function;

@Component
@Slf4j
public class DataFunction implements Function<AuthData,JwtTokenResponse>
{
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataFunction.class); //se @Slf4j non funzia
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	@Qualifier("customUserDetailsService")
	private UserDetailsService userDetailsService;
	
	@Override
	public JwtTokenResponse apply(AuthData data) 
	{
		log.info("Autenticazione e Generazione Token");

		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(data.getUsername());
		
		if (!passwordEncoder.matches(data.getPassword(), userDetails.getPassword()))
		{
			throw new BadCredentialsException("CREDENZIALI NON VALIDE");
		}

		final String token = jwtTokenUtil.generateToken(userDetails);
		
		log.info(String.format("Token %s", token));
		
		return new JwtTokenResponse(token);
	}
}
