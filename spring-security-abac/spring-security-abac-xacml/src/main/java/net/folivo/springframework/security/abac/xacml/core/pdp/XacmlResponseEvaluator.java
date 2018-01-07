package net.folivo.springframework.security.abac.xacml.core.pdp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.authzforce.core.pdp.api.DecisionResult;

import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;

public class XacmlResponseEvaluator implements ResponseEvaluator<DecisionResult> {

	Log log = LogFactory.getLog(XacmlResponseEvaluator.class);

	// TODO throw exception if problems
	@Override
	public boolean evaluateToBoolean(DecisionResult response) {
		DecisionType result = response.getDecision();
		log.debug(result);
		return result == DecisionType.PERMIT;
	}

}
