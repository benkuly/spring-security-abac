package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public interface RequestAttributeProvider<T> {

	Collection<RequestAttribute> getAttributes(T context);

}
