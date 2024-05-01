package it.alnao.lambda;

public class Saluti {
	public String handler(String nome) {
		return String.format("Saluti, %s benvenuto nel mondo lambda", nome);
	}
}
