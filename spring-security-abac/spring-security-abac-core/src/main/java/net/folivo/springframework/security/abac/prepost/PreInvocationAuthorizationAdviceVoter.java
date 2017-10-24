package net.folivo.springframework.security.abac.prepost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.Request;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.Response;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;

public class PreInvocationAuthorizationAdviceVoter implements AccessDecisionVoter<MethodInvocation> {

	private final ResponseEvaluator eval;
	private final RequestFactory requestFactory;
	private final PdpClient pdpClient;

	public PreInvocationAuthorizationAdviceVoter(RequestFactory requestFactory, PdpClient pdpClient,
			ResponseEvaluator eval) {
		this.requestFactory = requestFactory;
		this.pdpClient = pdpClient;
		this.eval = eval;
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return attribute instanceof PreInvocationAttribute;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return MethodInvocation.class.isAssignableFrom(clazz);
	}

	@Override
	public int vote(Authentication authentication, MethodInvocation method, Collection<ConfigAttribute> attributes) {

		List<ConfigAttribute> preAttrs = findPreInvocationAttributes(attributes);

		if (preAttrs.isEmpty()) {
			return ACCESS_ABSTAIN;
		}

		Request request = requestFactory.build(preAttrs);
		Response response = pdpClient.decide(request);
		boolean allowed = eval.evaluateToBoolean(response);

		return allowed ? ACCESS_GRANTED : ACCESS_DENIED;
	}

	private List<ConfigAttribute> findPreInvocationAttributes(Collection<ConfigAttribute> config) {
		List<ConfigAttribute> attrs = new ArrayList<>();
		for (ConfigAttribute attribute : config) {
			if (attribute instanceof PreInvocationAttribute) {
				attrs.add(attribute);
			}
		}
		return attrs;
	}

}
