package net.folivo.springframework.security.abac.prepost;

import org.springframework.security.access.prepost.PostInvocationAttribute;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public class AbacPostInvocationAttribute extends RequestAttribute implements PostInvocationAttribute {

	public AbacPostInvocationAttribute(AttributeCategory category, String id, Object value) {
		super(category, id, value);
	}

	@Override
	public String getAttribute() {
		return null;
	}
}
