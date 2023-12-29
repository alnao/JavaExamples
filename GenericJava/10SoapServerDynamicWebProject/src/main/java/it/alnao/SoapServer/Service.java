package it.alnao.SoapServer;

public interface Service {
	public boolean addPerson(Persona p);
	public boolean deletePerson(int id);
	public Persona getPerson(int id);
	public Persona[] getAllPersons();
}
