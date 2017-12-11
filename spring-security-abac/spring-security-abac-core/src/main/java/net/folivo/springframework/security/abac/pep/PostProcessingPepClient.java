package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public class PostProcessingPepClient<T> implements PepClient<T> {

	private final PepEngine pep;
	private final Collection<RequestAttributePostProcessor<T>> processors;

	public PostProcessingPepClient(PepEngine pep, Collection<RequestAttributePostProcessor<T>> processors) {
		this.pep = pep;
		this.processors = processors;
	}

	protected Collection<RequestAttribute> postProcess(Collection<RequestAttribute> attributes, T context) {
		// TODO caching/performance!
		// TODO maybe allow multiple processors for one attribute
		return attributes.stream()
				.flatMap(a -> processors.stream().filter(p -> p.supportsValue(a.getValue().getClass())).findFirst()
						.map(p -> p.process(a, context)).orElse(Collections.singleton(a)).stream())
				.collect(Collectors.toList());
	}

	@Override
	public boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes, T context) {
		return pep.buildRequestAndEvaluateToBoolean(postProcess(attributes, context));
	}
}
