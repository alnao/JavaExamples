package com.xantrix.servless.dao;

import com.xantrix.servless.model.Cliente;


public interface ClienteCrudDao 
{
	public Cliente SelClienteByCode(String CodFid);
	
	public void insCliente(Cliente cliente);
	
}
