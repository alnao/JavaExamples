package it.alnao.alNaoPassword;

import java.util.List;

import org.springframework.data.domain.Pageable;


public interface PasswordService 
{
	public PasswordEntities selByName(String name);
	
	public void delPwd(PasswordEntities pwd);
	
	public void insPwd(PasswordEntities pwd);
	
	public List<PasswordEntities> selTutti();

}
