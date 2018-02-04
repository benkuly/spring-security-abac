package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public abstract class RequestAttributesHolder implements ConfigAttribute {

	private final Collection<RequestAttribute> attributes;

	public RequestAttributesHolder(Collection<RequestAttribute> attributes) {
		this.attributes = attributes;
	}

	public Collection<RequestAttribute> getAttributes() {
		return attributes;
	}

}
