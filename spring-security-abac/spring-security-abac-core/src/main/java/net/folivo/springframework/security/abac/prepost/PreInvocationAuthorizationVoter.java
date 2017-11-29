package net.folivo.springframework.security.abac.prepost;

import java.util.ArrayList;
import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.RequestHolder;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;
import net.folivo.springframework.security.abac.pdp.ResponseHolder;

public class PreInvocationAuthorizationVoter implements AccessDecisionVoter<MethodInvocation> {

	private final ResponseEvaluator eval;
	private final RequestFactory requestFactory;
	private final PdpClient pdpClient;

	public PreInvocationAuthorizationVoter(RequestFactory requestFactory, PdpClient pdpClient, ResponseEvaluator eval) {
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

		Collection<RequestAttribute> requestAttrs = new ArrayList<>();

		// TODO post process!
		// .filter(a -> isPreInvocationAttribute(a)).map(a -> (RequestAttribute) a)

		if (requestAttrs.isEmpty()) {
			return ACCESS_ABSTAIN;
		}

		RequestHolder request = requestFactory.build(requestAttrs);
		ResponseHolder response = pdpClient.decide(request);
		boolean allowed = eval.evaluateToBoolean(response);

		return allowed ? ACCESS_GRANTED : ACCESS_DENIED;
	}

	private boolean isPreInvocationAttribute(ConfigAttribute attribute) {
		return attribute instanceof PreInvocationAttribute && attribute instanceof RequestAttribute;
	}

}
