package it.alnao.SoapServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceImpl implements Service {

	private static Map<Integer,Persona> persons = new HashMap<Integer,Persona>();
	
	@Override
	public boolean addPerson(Persona p) {
		if(persons.get(p.getId()) != null) 
			return false;
		persons.put(p.getId(), p);
		return true;
	}

	@Override
	public boolean deletePerson(int id) {
		if(persons.get(id) == null) 
			return false;
		persons.remove(id);
		return true;
	}

	@Override
	public Persona getPerson(int id) {
		return persons.get(id);
	}

	@Override
	public Persona[] getAllPersons() {
		Set<Integer> ids = persons.keySet();
		Persona[] p = new Persona[ids.size()];
		int i=0;
		for(Integer id : ids){
			p[i] = persons.get(id);
			i++;
		}
		return p;
	}


}
