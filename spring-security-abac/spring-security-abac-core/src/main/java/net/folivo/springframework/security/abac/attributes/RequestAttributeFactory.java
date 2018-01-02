package net.folivo.springframework.security.abac.attributes;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;

public interface RequestAttributeFactory {

	RequestAttribute build(AttributeCategory category, String id, String datatype, Object value);

}
