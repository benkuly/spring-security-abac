package net.folivo.springframework.security.abac.demo.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.folivo.springframework.security.abac.demo.filter.Filterable;

@Entity
public class User implements Filterable {

	@Id
	@GeneratedValue
	private long id;

	private String role;

	@JsonIgnore
	private String password;
	private String forename;
	private String surname;
	private String username;

	private User owner;

	@ManyToOne
	private Company company;

	protected User() {

	}

	public User(String username, String password, String role, String forename, String surname, User owner,
			Company company) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.forename = forename;
		this.surname = surname;
		this.owner = owner;
		this.company = company;
	}

	public long getId() {
		return id;
	}

	public String getRole() {
		return role;
	}

	public String getSurname() {
		return surname;
	}

	public String getForename() {
		return forename;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public Company getCompany() {
		return company;
	}

	public User getOwner() {
		return owner;
	}

}
