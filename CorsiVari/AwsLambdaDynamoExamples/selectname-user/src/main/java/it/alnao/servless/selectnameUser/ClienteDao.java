package it.alnao.servless.selectnameUser;

import java.util.List;

public interface ClienteDao {
	public List<Clienti> selClienteByName(String name);
}
