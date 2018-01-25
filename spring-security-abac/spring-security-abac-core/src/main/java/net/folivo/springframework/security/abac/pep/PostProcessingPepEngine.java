package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.ProcessorUtils;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.contexthandler.RequestContextHandler;

public class PostProcessingPepEngine<T> implements PepEngine<T> {

	private final RequestContextHandler<T> handler;
	private final Collection<RequestAttributeProcessor<T>> processors;

	public PostProcessingPepEngine(RequestContextHandler<T> handler,
			Collection<RequestAttributeProcessor<T>> processors) {
		this.handler = handler;
		this.processors = processors;
	}

	protected Collection<RequestAttribute> postProcess(Collection<RequestAttribute> attrs, T context) {
		return ProcessorUtils.process(attrs, context, processors);
	}

	@Override
	public boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes, T context) {
		return handler.buildRequestAndEvaluateToBoolean(postProcess(attributes, context), context);
	}
}
