package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;

public interface RequestAttributeProcessor<T> {

	boolean supports(RequestAttribute attr);

	Collection<RequestAttribute> process(RequestAttribute attr, T context);

}
