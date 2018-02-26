package net.folivo.springframework.security.abac.context;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;

public interface RequestContextMapper<T> {

	static final String ATTR_ID = "contextId";
	static final AttributeCategory ATTR_CAT = AttributeCategory.ENVIRONMENT;

	String map(T context);

	T resolve(String identifier);

	void release(String identifier);

}
