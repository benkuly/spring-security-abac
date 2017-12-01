package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;

import org.springframework.security.access.prepost.PreInvocationAttribute;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public class AbacPreInvocationAttribute extends RequestAttributeHolder implements PreInvocationAttribute {

	public AbacPreInvocationAttribute(Collection<RequestAttribute> attributes) {
		super(attributes);
	}

	@Override
	public String getAttribute() {
		return null;
	}

}
