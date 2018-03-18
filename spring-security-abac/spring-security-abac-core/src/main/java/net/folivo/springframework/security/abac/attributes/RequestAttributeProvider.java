package net.folivo.springframework.security.abac.attributes;

import java.util.stream.Stream;

public interface RequestAttributeProvider<T> {

	Stream<RequestAttribute> getAttributes(T context);

}
