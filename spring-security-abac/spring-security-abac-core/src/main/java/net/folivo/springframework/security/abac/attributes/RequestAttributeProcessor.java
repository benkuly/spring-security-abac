package net.folivo.springframework.security.abac.attributes;

import java.util.stream.Stream;

public interface RequestAttributeProcessor<T> {

	boolean supports(RequestAttribute attr);

	Stream<RequestAttribute> process(RequestAttribute attr, T context);

}
