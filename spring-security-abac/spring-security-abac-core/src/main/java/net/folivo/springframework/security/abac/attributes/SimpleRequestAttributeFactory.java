package net.folivo.springframework.security.abac.attributes;

public class SimpleRequestAttributeFactory implements RequestAttributeFactory {

	@Override
	public RequestAttribute build(AttributeCategory category, String id, Object value) {
		return new SimpleRequestAttribute(new SimpleRequestAttributeMetadata(category, id), value);
	}

}
