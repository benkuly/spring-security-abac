package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public interface RequestAttributeProcessor<T> {

	boolean supportsValue(Class<?> clazz);

	Collection<RequestAttribute> process(RequestAttribute attr, T context);

}
