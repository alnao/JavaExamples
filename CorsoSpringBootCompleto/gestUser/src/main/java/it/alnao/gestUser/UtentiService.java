package it.alnao.gestUser;

import java.util.List;

public interface UtentiService
{
	public List<Utenti> SelTutti();
	
	public Utenti SelUser(String UserId);
	
	public void Save(Utenti utente);
	
	public void Delete(Utenti utente);
	
}
