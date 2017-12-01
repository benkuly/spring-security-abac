package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;

import org.springframework.security.access.prepost.PostInvocationAttribute;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public class AbacPostInvocationAttribute extends RequestAttributeHolder implements PostInvocationAttribute {

	public AbacPostInvocationAttribute(Collection<RequestAttribute> attributes) {
		super(attributes);
	}

	@Override
	public String getAttribute() {
		return null;
	}

}
