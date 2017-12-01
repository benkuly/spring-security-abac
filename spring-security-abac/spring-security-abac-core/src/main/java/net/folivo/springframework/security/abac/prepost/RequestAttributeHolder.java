package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public abstract class RequestAttributeHolder {

	private final Collection<RequestAttribute> attributes;

	public RequestAttributeHolder(Collection<RequestAttribute> attributes) {
		this.attributes = attributes;
	}

	public Collection<RequestAttribute> getAttributes() {
		return attributes;
	}

}
