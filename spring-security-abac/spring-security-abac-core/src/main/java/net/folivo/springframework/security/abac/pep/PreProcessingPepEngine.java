package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.ProcessorUtils;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.pdp.PdpClient;

public class PreProcessingPepEngine<T> implements PepEngine<T> {

	private final PdpClient<T> pdp;
	private final Collection<RequestAttributeProcessor<T>> processors;

	public PreProcessingPepEngine(PdpClient<T> pdp,
			Collection<RequestAttributeProcessor<T>> processors) {
		this.pdp = pdp;
		this.processors = processors;
	}

	protected Collection<RequestAttribute> postProcess(T context, Collection<RequestAttribute> attrs) {
		return ProcessorUtils.process(attrs, context, processors);
	}

	@Override
	public PepResponse decide(T context, Collection<RequestAttribute> attributes) {
		return pdp.decide(context, postProcess(context, attributes));
	}

	@Override
	public PepResponse decide(T context) {
		throw new UnsupportedOperationException("currently not supported");
	}
}
