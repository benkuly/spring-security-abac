package net.folivo.springframework.security.abac.demo.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.hateoas.Identifiable;

import net.folivo.springframework.security.abac.demo.filter.Filterable;

@Entity
public class Posting implements Filterable, Identifiable<Long> {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private User creator;
	private LocalDateTime creationTime;
	private String content;

	protected Posting() {

	}

	public Posting(User creator, LocalDateTime creationTime, String content) {
		this.creator = creator;
		this.creationTime = creationTime;
		this.content = content;
	}

	@Override
	public Long getId() {
		return id;
	}

	public User getCreator() {
		return creator;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public String getContent() {
		return content;
	}

}
