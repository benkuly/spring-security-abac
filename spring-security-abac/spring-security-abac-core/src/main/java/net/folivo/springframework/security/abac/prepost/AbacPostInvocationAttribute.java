package net.folivo.springframework.security.abac.prepost;

import org.springframework.security.access.prepost.PostInvocationAttribute;

import net.folivo.springframework.security.abac.pdp.PdpRequestAttribute;

public class AbacPostInvocationAttribute extends PdpRequestAttribute implements PostInvocationAttribute {

	public AbacPostInvocationAttribute(AttributeCategory category, String id, String datatype, Object value) {
		super(category, id, datatype, value);
	}

	@Override
	public String getAttribute() {
		return null;
	}
}
