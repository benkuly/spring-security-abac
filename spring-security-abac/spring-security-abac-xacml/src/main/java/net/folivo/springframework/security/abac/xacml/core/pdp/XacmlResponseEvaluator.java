package net.folivo.springframework.security.abac.xacml.core.pdp;

import java.util.Collection;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.Result;

import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;

public class XacmlResponseEvaluator implements ResponseEvaluator<Response> {

	Log log = LogFactory.getLog(XacmlResponseEvaluator.class);

	// TODO throw exception if problems
	@Override
	public boolean evaluateToBoolean(Response response) {
		Collection<Result> results = response.getResults();
		Optional<Result> firstResult = results.stream().findFirst();
		if (firstResult.isPresent()) {
			Result result = firstResult.get();
			log.debug(result);
			return result.getDecision().getBasicDecision() == Decision.PERMIT;
		} else {
			log.debug("didn't get any result");
			return false;
		}
	}

}
