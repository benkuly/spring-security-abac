package net.folivo.springframework.security.abac.pdp;

public interface RequestAttributeFactory {

	RequestAttribute build(AttributeCategory category, String id, String datatype, Object value);

}
