package it.alnao.southAfricanMobileNumbers.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

//import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "SAMN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SouthAfricanMobileNumbersEntity implements Serializable {
	
	private static final long serialVersionUID = -8385491839199852329L;

	@Id
	private String id;
	
	@Field(name="idNumber")
	private String idNumber;
	
	@Field(name="phoneNumber")
	private String phoneNumber;	
	
	@Field(name="type")
	private String type;	
	
	@Field(name="loadDate")
	private String loadDate;
	
	@Override
	public String toString() {
		return "SouthAfricanMobileNumbersEntity [id=" + id + ", idNumber=" + idNumber + ", phoneNumber=" + phoneNumber
				+ ", type=" + type + ", loadDate=" + loadDate + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(String loadDate) {
		this.loadDate = loadDate;
	}


}
