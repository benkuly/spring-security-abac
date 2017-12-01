package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.stream.Collectors;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.PdpRequest;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.PdpResponse;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;

public class PepEngineImpl<T> implements PepEngine<T> {

	private final PdpClient pdp;
	private final Collection<RequestAttributePostProcessor<T>> processors;
	private final ResponseEvaluator evaluator;
	private final RequestFactory requestFactory;

	public PepEngineImpl(PdpClient pdp, Collection<RequestAttributePostProcessor<T>> processors,
			ResponseEvaluator evaluator, RequestFactory requestFactory) {
		this.pdp = pdp;
		this.processors = processors;
		this.evaluator = evaluator;
		this.requestFactory = requestFactory;
	}

	protected Collection<RequestAttribute> postProcess(Collection<RequestAttribute> attributes, T context) {

		// TODO caching/performance!
		return attributes.stream().map(a -> processors.stream().filter(p -> p.supportsValue(a.getValue().getClass()))
				.findFirst().map(p -> p.process(a, context)).orElse(a)).collect(Collectors.toList());
	}

	@Override
	public PdpResponse buildRequest(Collection<RequestAttribute> attributes) {
		PdpRequest request = requestFactory.build(attributes);
		return pdp.decide(request);
	}

	@Override
	public boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes) {
		return evaluator.evaluateToBoolean(buildRequest(attributes));
	}

}
