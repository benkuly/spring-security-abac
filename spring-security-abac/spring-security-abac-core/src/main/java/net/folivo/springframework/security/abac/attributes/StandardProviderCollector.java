package net.folivo.springframework.security.abac.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

//TODO javadoc: providers and processors must be sorted!
public class StandardProviderCollector<T> implements ProviderCollector<T> {

	private final List<RequestAttributeProvider<T>> providers;

	public StandardProviderCollector(List<RequestAttributeProvider<T>> providers) {
		this.providers = providers;
	}

	@Override
	public Stream<RequestAttribute> collect(T context) {
		Map<RequestAttributeMetadata, RequestAttribute> attrs = new HashMap<>();

		for (int i = providers.size() - 1; i >= 0; i--) {
			providers.get(i).getAttributes(context)
					.filter(a -> attrs.containsKey(a.getMetadata()))
					.forEach(a -> attrs.put(a.getMetadata(), a));
		}

		return attrs.values().stream();
	}
}
