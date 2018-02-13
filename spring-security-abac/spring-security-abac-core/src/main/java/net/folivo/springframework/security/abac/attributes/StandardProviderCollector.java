package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//TODO force that providers and processors are sorted!
public class StandardProviderCollector<T> implements ProviderCollector<T> {

	private final List<RequestAttributeProvider<T>> providers;

	public StandardProviderCollector(List<RequestAttributeProvider<T>> providers) {
		this.providers = providers;
	}

	@Override
	public Collection<RequestAttribute> collectAll(T context) {
		// TODO bad solution (instead of put, go backwards and use containsKey)
		Map<RequestAttributeMetadata, RequestAttribute> attrs = new HashMap<>();
		providers.stream().flatMap(p -> p.getAttributes(context).stream()).forEach(a -> attrs.put(a.getMetadata(), a));
		return attrs.values();
	}

	@Override
	public Optional<RequestAttribute> collectFirst(T context) {
		return providers.stream().flatMap(p -> p.getAttributes(context).stream()).findFirst();
	}
}
