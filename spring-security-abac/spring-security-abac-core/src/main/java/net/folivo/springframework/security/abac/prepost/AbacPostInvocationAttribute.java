package net.folivo.springframework.security.abac.prepost;

import java.util.stream.Stream;

import org.springframework.security.access.prepost.PostInvocationAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public class AbacPostInvocationAttribute extends RequestAttributesHolder implements PostInvocationAttribute {

	public AbacPostInvocationAttribute(Stream<RequestAttribute> attributes) {
		super(attributes);
	}

	@Override
	public String getAttribute() {
		return null;
	}

}
