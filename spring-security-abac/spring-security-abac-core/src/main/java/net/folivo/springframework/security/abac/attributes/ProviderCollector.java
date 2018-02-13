package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;
import java.util.Optional;

public interface ProviderCollector<T> {

	Collection<RequestAttribute> collectAll(T context);

	Optional<RequestAttribute> collectFirst(T context);

}
