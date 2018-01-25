package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.attributes.ProcessorUtils;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;

//TODO force that providers and processors are sorted!
public class PreProcessingProviderCollector<T> implements ProviderCollector<T> {

	private final List<RequestAttributeProvider<T>> providers;
	private final List<RequestAttributeProcessor<T>> processors;
	private final ConfigAttributeFactory factory;

	public PreProcessingProviderCollector(List<RequestAttributeProvider<T>> providers,
			List<RequestAttributeProcessor<T>> processors, ConfigAttributeFactory factory) {
		this.providers = providers;
		this.processors = processors;
		this.factory = factory;
	}

	@Override
	public Collection<ConfigAttribute> collect(T context) {
		return factory.createConfigAttributes(preProcessAttributes(mergeAttribtesFromProvider(context), context));
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
