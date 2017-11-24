package net.folivo.springframework.security.abac.demo.entities;

import java.time.LocalDateTime;

public class PostingResource {
	private Long id;
	private String creatorUsername;
	private LocalDateTime creationTime;
	private String content;

	protected PostingResource() {

	}

	public PostingResource(Posting p) {
		this.id = p.getId();
		this.creatorUsername = p.getCreator().getUsername();
		this.creationTime = p.getCreationTime();
		this.content = p.getContent();
	}

	public Long getId() {
		return id;
	}

	public String getCreatorUsername() {
		return creatorUsername;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public String getContent() {
		return content;
	}

}
