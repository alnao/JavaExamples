package it.alnao.mywebsb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.alnao.mywebsb.entities.Password;
import it.alnao.mywebsb.repository.PasswordRepository;

@Service
@Transactional(readOnly = true)
public class CManagerServiceImpl implements CManagerService {
	
	@Autowired
	PasswordRepository passwordRepository;
	
	public List<Password> getListPassword(){
		return passwordRepository.findAll();
	}
	
	public Password getFirstPassword() {
		return passwordRepository.findAll().get(0);
	}
}
