package net.folivo.springframework.security.abac.pdp;

public class SimpleRequestAttributeFactory implements RequestAttributeFactory {

	@Override
	public RequestAttribute build(AttributeCategory category, String id, String datatype, Object value) {
		return new SimpleRequestAttribute(category, id, datatype, value);
	}

}
