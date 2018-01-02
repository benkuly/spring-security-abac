package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;

import org.springframework.security.access.prepost.PreInvocationAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.pep.RequestAttributesHolder;

public class AbacPreInvocationAttribute extends RequestAttributesHolder implements PreInvocationAttribute {

	public AbacPreInvocationAttribute(Collection<RequestAttribute> attributes) {
		super(attributes);
	}

	@Override
	public String getAttribute() {
		return null;
	}

}
