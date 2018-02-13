package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;
import java.util.List;

//TODO force that providers and processors are sorted!
public class PreProcessingProviderCollector<T> extends StandardProviderCollector<T> {

	private final List<RequestAttributeProcessor<T>> processors;

	public PreProcessingProviderCollector(List<RequestAttributeProvider<T>> providers,
			List<RequestAttributeProcessor<T>> processors) {
		super(providers);
		this.processors = processors;
	}

	@Override
	public Collection<RequestAttribute> collectAll(T context) {
		return preProcessAttributes(super.collectAll(context), context);
	}

	protected Collection<RequestAttribute> preProcessAttributes(Collection<RequestAttribute> attrs, T context) {
		// TODO caching/performance!
		// TODO maybe allow multiple processors for one attribute
		// TODO outsource to util, because copy-paste from PostProcessingPepClient
		return ProcessorUtils.process(attrs, context, processors);
	}
}
