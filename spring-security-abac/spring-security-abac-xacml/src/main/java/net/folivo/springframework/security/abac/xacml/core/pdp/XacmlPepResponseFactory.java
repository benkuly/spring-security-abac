package net.folivo.springframework.security.abac.xacml.core.pdp;

import org.ow2.authzforce.core.pdp.api.DecisionResult;

import net.folivo.springframework.security.abac.pep.PepResponse;
import net.folivo.springframework.security.abac.pep.PepResponseFactory;

public class XacmlPepResponseFactory implements PepResponseFactory<DecisionResult> {

	@Override
	public PepResponse build(DecisionResult pdpResponse) {
		return new XacmlPepResponse(pdpResponse);
	}

}
