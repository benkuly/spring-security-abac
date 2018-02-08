package net.folivo.springframework.security.abac.xacml.core.pdp;

import org.ow2.authzforce.core.pdp.api.DecisionRequest;
import org.ow2.authzforce.core.pdp.api.DecisionResult;
import org.ow2.authzforce.core.pdp.api.PdpEngine;

import net.folivo.springframework.security.abac.contexthandler.PdpClient;

public class XacmlPdpClient<T> implements PdpClient<DecisionRequest, DecisionResult, T> {

	private final PdpEngine engine;

	public XacmlPdpClient(PdpEngine engine) {
		this.engine = engine;
	}

	// TODO should throw a in core defined exception!
	// TODO should use the context
	@Override
	public DecisionResult decide(DecisionRequest request, T context) {
		DecisionResult response = engine.evaluate(request);
		return response;
	}
}
