package it.alnao.AuthServer.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component 
@ConfigurationProperties("gestuser")
@Data
public class UserConfig 
{
	private String srvUrl;
	private String userId;
	private String password;
	public String getSrvUrl() {
		return srvUrl;
	}
	public void setSrvUrl(String srvUrl) {
		this.srvUrl = srvUrl;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
