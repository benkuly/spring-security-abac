package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO force that providers and processors are sorted!
public class PreProcessingProviderCollector<T> implements ProviderCollector<T> {

	private final List<RequestAttributeProvider<T>> providers;
	private final List<RequestAttributeProcessor<T>> processors;

	public PreProcessingProviderCollector(List<RequestAttributeProvider<T>> providers,
			List<RequestAttributeProcessor<T>> processors) {
		this.providers = providers;
		this.processors = processors;
	}

	@Override
	public Collection<RequestAttribute> collect(T context) {
		return preProcessAttributes(mergeAttribtesFromProvider(context), context);
	}

	protected Collection<RequestAttribute> mergeAttribtesFromProvider(T context) {
		// TODO bad solution (instead of put, go backwards and use containsKey)
		Map<RequestAttributeMetadata, RequestAttribute> attrs = new HashMap<>();
		providers.stream().flatMap(p -> p.getAttributes(context).stream()).forEach(a -> attrs.put(a.getMetadata(), a));
		return attrs.values();
	}

	protected Collection<RequestAttribute> preProcessAttributes(Collection<RequestAttribute> attrs, T context) {
		// TODO caching/performance!
		// TODO maybe allow multiple processors for one attribute
		// TODO outsource to util, because copy-paste from PostProcessingPepClient
		return ProcessorUtils.process(attrs, context, processors);
	}
}
