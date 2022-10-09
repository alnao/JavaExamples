package it.alnao.alNaoPassword;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PasswordServiceImpl implements PasswordService
{
	@Autowired
	PasswordRepository passwordRepository;

	@Override
	@Cacheable
	public PasswordEntities selByName(String name){
		return passwordRepository.findByNome (name.toUpperCase());
	}

	@Override
	@Transactional
	public void delPwd(PasswordEntities pwd){
		passwordRepository.delete(pwd);
	}

	@Override
	@Transactional
	public void insPwd(PasswordEntities pwd){
		passwordRepository.save(pwd);
	}
	
	@Override
	@Transactional
	public List<PasswordEntities> selTutti(){
		return passwordRepository.findAll();
	}
	
	
}

