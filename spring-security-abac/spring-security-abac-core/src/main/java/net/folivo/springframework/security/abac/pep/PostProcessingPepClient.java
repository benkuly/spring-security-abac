package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.ProcessorUtils;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;

public class PostProcessingPepClient<T> implements PepClient<T> {

	private final PepEngine pep;
	private final Collection<RequestAttributeProcessor<T>> processors;

	public PostProcessingPepClient(PepEngine pep, Collection<RequestAttributeProcessor<T>> processors) {
		this.pep = pep;
		this.processors = processors;
	}

	protected Collection<RequestAttribute> postProcess(Collection<RequestAttribute> attrs, T context) {
		return ProcessorUtils.process(attrs, context, processors);
	}

	@Override
	public boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes, T context) {
		return pep.buildRequestAndEvaluateToBoolean(postProcess(attributes, context));
	}
}
