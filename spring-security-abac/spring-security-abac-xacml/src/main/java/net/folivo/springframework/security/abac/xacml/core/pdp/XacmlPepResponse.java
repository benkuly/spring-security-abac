package net.folivo.springframework.security.abac.xacml.core.pdp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.authzforce.core.pdp.api.DecisionResult;

import net.folivo.springframework.security.abac.pep.PepResponse;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;

public class XacmlPepResponse implements PepResponse {

	private final Log log = LogFactory.getLog(XacmlPepResponse.class);

	private final DecisionResult response;

	public XacmlPepResponse(DecisionResult response) {
		this.response = response;
	}

	// TODO throw exception if problems
	@Override
	public boolean evaluateToBoolean() {
		DecisionType result = response.getDecision();
		if (log.isDebugEnabled())
			log.debug("The decison result was " + result);
		return result == DecisionType.PERMIT;
	}

}
