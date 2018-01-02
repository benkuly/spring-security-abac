package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;

public class SimplePepEngine<R, S> implements PepEngine {

	private final PdpClient<R, S> pdp;
	private final ResponseEvaluator<S> evaluator;
	private final RequestFactory<R> requestFactory;

	public SimplePepEngine(PdpClient<R, S> pdp, ResponseEvaluator<S> evaluator, RequestFactory<R> requestFactory) {
		this.pdp = pdp;
		this.evaluator = evaluator;
		this.requestFactory = requestFactory;
	}

	@Override
	public boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes) {
		R request = requestFactory.build(attributes);
		S response = pdp.decide(request);
		return evaluator.evaluateToBoolean(response);
	}
}
