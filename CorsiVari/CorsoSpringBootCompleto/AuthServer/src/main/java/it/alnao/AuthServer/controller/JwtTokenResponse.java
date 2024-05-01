package it.alnao.AuthServer.controller;

import java.io.Serializable;

import lombok.Data;

@Data
public class JwtTokenResponse implements Serializable 
{

	private static final long serialVersionUID = 8317676219297719109L;

	private String token;

	public JwtTokenResponse(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}