package net.folivo.springframework.security.abac.xacml.core.pdp;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Response;

import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;

public class XacmlResponseEvaluator implements ResponseEvaluator<Response> {

	// TODO throw exception if problems
	@Override
	public boolean evaluateToBoolean(Response response) {
		return response.getResults().stream().findFirst()
				.map(r -> r.getDecision().getBasicDecision() == Decision.PERMIT).orElse(false);
	}

}
