package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;

public interface RequestAttributeProvider<T> {

	Collection<RequestAttribute> getAttributes(T context);

}
