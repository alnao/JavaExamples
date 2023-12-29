package it.alnao.SoapServer;

import java.io.Serializable;

public class Persona  implements Serializable{
	private static final long serialVersionUID = 5541301676282845455L;
	private String name;
	private int age;
	private int id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Persona [name=" + name + ", age=" + age + ", id=" + id + "]";
	}
	
	
}
