package it.alnao.servless.selectbolUser;

import java.util.List;

public interface ClienteDao {
	public List<Clienti>  selClienteByBollini(Filtro filtro);
}
