package net.folivo.springframework.security.abac.attributes;

import java.util.stream.Stream;

public interface ProviderCollector<T> {

	Stream<RequestAttribute> collect(T context);

}
