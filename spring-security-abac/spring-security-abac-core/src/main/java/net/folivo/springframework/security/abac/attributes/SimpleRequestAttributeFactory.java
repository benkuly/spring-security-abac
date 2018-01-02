package net.folivo.springframework.security.abac.attributes;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;

public class SimpleRequestAttributeFactory implements RequestAttributeFactory {

	@Override
	public RequestAttribute build(AttributeCategory category, String id, String datatype, Object value) {
		return new SimpleRequestAttribute(category, id, datatype, value);
	}

}
