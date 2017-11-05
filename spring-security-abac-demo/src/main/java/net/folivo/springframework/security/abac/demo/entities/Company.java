package net.folivo.springframework.security.abac.demo.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Company {

	@Id
	@GeneratedValue
	private long id;

	private String name;
	private String adress;
	private String email;
	private String phone;

	protected Company() {
	}

	public Company(String name, String adress, String email, String phone) {
		this.name = name;
		this.adress = adress;
		this.email = email;
		this.phone = phone;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAdress() {
		return adress;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

}
