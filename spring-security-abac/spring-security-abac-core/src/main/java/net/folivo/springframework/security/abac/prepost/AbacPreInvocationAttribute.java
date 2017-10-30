package net.folivo.springframework.security.abac.prepost;

import org.springframework.security.access.prepost.PreInvocationAttribute;

import net.folivo.springframework.security.abac.pdp.PdpRequestAttribute;

public class AbacPreInvocationAttribute extends PdpRequestAttribute implements PreInvocationAttribute {

	public AbacPreInvocationAttribute(AttributeCategory category, String id, String datatype, Object value) {
		super(category, id, datatype, value);
	}

	@Override
	public String getAttribute() {
		return null;
	}

}
