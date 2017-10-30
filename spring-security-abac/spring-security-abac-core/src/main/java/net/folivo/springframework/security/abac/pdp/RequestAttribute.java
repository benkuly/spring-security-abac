package net.folivo.springframework.security.abac.pdp;

import net.folivo.springframework.security.abac.prepost.AttributeCategory;

public class RequestAttribute {

	private final AttributeCategory category;
	private final String id;
	private final String datatype;
	private final Object value;

	public RequestAttribute(AttributeCategory category, String id, String datatype, Object value) {
		this.category = category;
		this.id = id;
		this.datatype = datatype;
		this.value = value;
	}

	public AttributeCategory getCategory() {
		return category;
	}

	public String getId() {
		return id;
	}

	public String getDatatype() {
		return datatype;
	}

	public Object getValue() {
		return value;
	}

}
