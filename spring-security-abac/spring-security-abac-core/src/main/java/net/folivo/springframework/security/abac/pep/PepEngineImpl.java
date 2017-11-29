package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public abstract class PepEngineImpl<T> {

	private final PdpClient pdp;
	private final Collection<RequestAttributeProvider<T>> providers;
	Collection<RequestAttributePostProcessor<T>> processors;

	public PepEngineImpl(PdpClient pdp, Collection<RequestAttributeProvider<T>> providers,
			Collection<RequestAttributePostProcessor<T>> processors) {
		this.pdp = pdp;
		this.providers = providers;
		this.processors = processors;
	}

	protected Collection<RequestAttribute> collectAttributes(T context) {
		Map<String, RequestAttribute> set = new HashMap<>();

		// TODO may have performance issues due to "put" -> maybe order backward and
		// don't put, if value exists
		providers.stream().sorted(AnnotationAwareOrderComparator.INSTANCE)
				.flatMap(p -> p.getAttributes(context).stream()).forEachOrdered(a -> set.put(a.getId(), a));

		return set.values();
	}

	protected Collection<RequestAttribute> postProcess(Collection<RequestAttribute> attributes, T context) {

		// TODO caching/performance!
		return attributes.stream()
				.map(a -> processors.stream().filter(f -> f.supportsValue(a.getValue().getClass()))
						.map(f -> f.process(a, context)).findFirst())
				.filter(Optional<RequestAttribute>::isPresent).map(Optional<RequestAttribute>::get)
				.collect(Collectors.toList());
	}

}
