package it.alnao.mavenExamples;

public class PrimoBean{
	private String utente;
	
	public PrimoBean(String valore) {
		setUtente(valore);
	}

	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}
	
}