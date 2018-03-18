package net.folivo.springframework.security.abac.prepost;

import java.util.stream.Stream;

import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public abstract class RequestAttributesHolder implements ConfigAttribute {

	private final Stream<RequestAttribute> attributes;

	public RequestAttributesHolder(Stream<RequestAttribute> attributes) {
		this.attributes = attributes;
	}

	public Stream<RequestAttribute> getAttributes() {
		return attributes;
	}

}
