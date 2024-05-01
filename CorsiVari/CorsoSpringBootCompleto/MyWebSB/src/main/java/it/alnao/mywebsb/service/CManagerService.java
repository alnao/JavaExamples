package it.alnao.mywebsb.service;

import java.util.List;

import it.alnao.mywebsb.entities.Password;

public interface CManagerService {
	public List<Password> getListPassword();
	public Password getFirstPassword();
}
