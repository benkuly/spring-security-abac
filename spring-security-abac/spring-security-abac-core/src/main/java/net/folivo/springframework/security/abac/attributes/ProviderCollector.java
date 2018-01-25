package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;

public interface ProviderCollector<T> {

	Collection<RequestAttribute> collect(T context);

}
