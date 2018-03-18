package net.folivo.springframework.security.abac.prepost;

import java.util.stream.Stream;

import org.springframework.security.access.prepost.PreInvocationAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public class AbacPreInvocationAttribute extends RequestAttributesHolder implements PreInvocationAttribute {

	public AbacPreInvocationAttribute(Stream<RequestAttribute> attributes) {
		super(attributes);
	}

	@Override
	public String getAttribute() {
		return null;
	}

}
