package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

//TODO maybe other package
public abstract class RequestAttributesHolder {

	private final Collection<RequestAttribute> attributes;

	public RequestAttributesHolder(Collection<RequestAttribute> attributes) {
		this.attributes = attributes;
	}

	public Collection<RequestAttribute> getAttributes() {
		return attributes;
	}

}
