package net.folivo.springframework.security.abac.xacml.core.pdp;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;

//TODO this into core?
public class XacmlRequestAttributeFactory implements RequestAttributeFactory {

	@Override
	public RequestAttribute build(AttributeCategory category, String id, String datatype, Object value) {
		return new XacmlRequestAttribute(category, id, datatype, value);
	}

}
