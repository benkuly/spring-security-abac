package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;

public interface RequestAttributeProvider<T> {

	Collection<RequestAttribute> getAttributes(T context);

	// TODO mention that not class support is meant, but e.g. parameters of the
	// context. So more like: do you possibly provide any attributes with this
	// context
	boolean supports(T context);

}
