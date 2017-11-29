package net.folivo.springframework.security.abac.pep;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public interface RequestAttributePostProcessor<T> {

	boolean supportsValue(Class<?> clazz);

	RequestAttribute process(RequestAttribute attr, T context);

}
