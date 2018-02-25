package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.ProcessorUtils;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.contexthandler.RequestContextHandler;

public class PreProcessingPepEngine<T> implements PepEngine<T> {

	private final RequestContextHandler<T> handler;
	private final Collection<RequestAttributeProcessor<T>> processors;

	public PreProcessingPepEngine(RequestContextHandler<T> handler,
			Collection<RequestAttributeProcessor<T>> processors) {
		this.handler = handler;
		this.processors = processors;
	}

	protected Collection<RequestAttribute> postProcess(T context, Collection<RequestAttribute> attrs) {
		return ProcessorUtils.process(attrs, context, processors);
	}

	@Override
	public PepResponse decide(T context, Collection<RequestAttribute> attributes) {
		return handler.decide(context, postProcess(context, attributes));
	}

	@Override
	public PepResponse decide(T context) {
		throw new UnsupportedOperationException("currently not supported");
	}
}
