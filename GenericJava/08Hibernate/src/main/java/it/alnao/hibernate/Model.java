package it.alnao.hibernate;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name= "test_alberto",	   uniqueConstraints={@UniqueConstraint(columnNames={"id"} ) } ) 
public class Model{

	private long id;
	private String nome;
	private String cognome;
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Column(name = "nome")
 	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	@Column(name = "cognome")
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@Override
	public String toString() {
		return "Model [id=" + id + ", nome=" + nome + ", cognome=" + cognome + "]";
	}
}
