package net.folivo.springframework.security.abac.attributes;

public class StandardRequestAttributeFactory implements RequestAttributeFactory {

	@Override
	public RequestAttribute build(AttributeCategory category, String id, Object value) {
		return new StandardRequestAttribute(new StandardRequestAttributeMetadata(category, id), value);
	}

}
