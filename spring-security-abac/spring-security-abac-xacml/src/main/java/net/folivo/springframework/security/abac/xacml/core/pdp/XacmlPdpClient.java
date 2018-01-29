package net.folivo.springframework.security.abac.xacml.core.pdp;

import org.ow2.authzforce.core.pdp.api.DecisionRequest;
import org.ow2.authzforce.core.pdp.api.DecisionResult;
import org.ow2.authzforce.core.pdp.api.PdpEngine;

import net.folivo.springframework.security.abac.contexthandler.PdpClient;

public class XacmlPdpClient implements PdpClient<DecisionRequest, DecisionResult> {

	private final PdpEngine engine;

	public XacmlPdpClient(PdpEngine engine) {
		this.engine = engine;
	}

	// TODO should throw a in core defined exception!
	@Override
	public DecisionResult decide(DecisionRequest request) {
		DecisionResult response = engine.evaluate(request);
		return response;
	}
}
