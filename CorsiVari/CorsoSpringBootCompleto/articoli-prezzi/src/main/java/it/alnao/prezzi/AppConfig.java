package it.alnao.prezzi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("applicazione") //prima parte delle properties
public class AppConfig{
	private String prezzo; //con get a set //seconda parte delle properties

	public String getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(String prezzo) {
		this.prezzo = prezzo;
	}

}