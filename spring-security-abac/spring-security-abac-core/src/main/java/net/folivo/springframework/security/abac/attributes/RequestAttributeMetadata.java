package net.folivo.springframework.security.abac.attributes;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;

public interface RequestAttributeMetadata {

	public AttributeCategory getCategory();

	public String getId();

	public String getDatatype();
}
