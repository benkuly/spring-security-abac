package net.folivo.springframework.security.abac.demo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import net.folivo.springframework.security.abac.demo.filter.Filterable;

@Entity
public class User implements Filterable, Identifiable<Long> {

	@Id
	@GeneratedValue
	private Long id;

	private String role;

	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	private String forename;
	private String surname;
	@Column(unique = true)
	private String username;
	private String email;

	protected User() {

	}

	public User(String username, String password, String role, String forename, String surname, String email) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.forename = forename;
		this.surname = surname;
		this.email = email;
	}

	@Override
	public Long getId() {
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

	public String getEmail() {
		return email;
	}

}
