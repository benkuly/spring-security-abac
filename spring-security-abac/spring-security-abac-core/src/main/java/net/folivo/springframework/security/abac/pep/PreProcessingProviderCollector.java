package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

//TODO force that providers and processors are sorted!
public class PreProcessingProviderCollector<T> implements ProviderCollector<T> {

	private static final Log log = LogFactory.getLog(PreProcessingProviderCollector.class);
	private final List<RequestAttributeProvider<T>> providers;
	private final List<RequestAttributePreProcessor<T>> processors;
	private final ConfigAttributeFactory factory;

	public PreProcessingProviderCollector(List<RequestAttributeProvider<T>> providers,
			List<RequestAttributePreProcessor<T>> processors, ConfigAttributeFactory factory) {
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
		Map<String, RequestAttribute> attrs = new HashMap<>();
		providers.stream().flatMap(p -> p.getAttributes(context).stream()).forEach(a -> attrs.put(a.getId(), a));
		return attrs.values();
	}

	protected Collection<RequestAttribute> preProcessAttributes(Collection<RequestAttribute> attrs, T context) {
		// TODO caching/performance!
		// TODO maybe allow multiple processors for one attribute
		// TODO outsource to util, because copy-paste from PostProcessingPepClient
		return attrs.stream().flatMap(a -> processors.stream().filter(p -> {
			Object value = a.getValue();
			if (value == null) {
				if (log.isDebugEnabled())
					log.debug("RequestAttribute with id '" + a.getId()
							+ "' will not be preprocessed because its value is null!");
				return false;
			}
			return p.supports(a);
		}).findFirst().map(p -> p.process(a, context)).orElse(Collections.singleton(a)).stream())
				.collect(Collectors.toList());
	}
}
