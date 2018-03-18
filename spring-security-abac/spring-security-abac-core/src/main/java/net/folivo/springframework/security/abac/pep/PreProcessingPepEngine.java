package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.stream.Stream;

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

	protected Stream<RequestAttribute> postProcess(T context, Stream<RequestAttribute> attrs) {
		return ProcessorUtils.process(attrs, context, processors);
	}

	@Override
	public PepResponse decide(T context, Stream<RequestAttribute> attributes) {
		return pdp.decide(context, postProcess(context, attributes));
	}

	@Override
	public PepResponse decide(T context) {
		throw new UnsupportedOperationException("currently not supported");
	}
}
