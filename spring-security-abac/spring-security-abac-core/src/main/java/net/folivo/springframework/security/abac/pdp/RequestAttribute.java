package net.folivo.springframework.security.abac.pdp;

import net.folivo.springframework.security.abac.prepost.AttributeCategory;

public class RequestAttribute {

	private final AttributeCategory category;
	private final String id;
	private final Object value;

	public RequestAttribute(AttributeCategory category, String id, Object value) {
		this.category = category;
		this.id = id;
		this.value = value;
	}

	public AttributeCategory getCategory() {
		return category;
	}

	public String getId() {
		return id;
	}

	public Object getValue() {
		return value;
	}

}
