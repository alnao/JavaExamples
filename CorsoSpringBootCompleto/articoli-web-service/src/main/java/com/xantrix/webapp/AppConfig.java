package com.xantrix.webapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("applicazione") //prima parte delle properties
public class AppConfig{
	private String listino; //con get a set //seconda parte delle properties

	public String getListino() {
		return listino;
	}

	public void setListino(String listino) {
		this.listino = listino;
	}
}