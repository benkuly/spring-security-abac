package net.folivo.springframework.security.abac.attributes;

public interface RequestAttributeFactory {

	RequestAttribute build(AttributeCategory category, String id, Object value);

}
