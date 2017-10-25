package net.folivo.springframework.security.abac.prepost;

import java.util.ArrayList;
import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.Request;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeConverter;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.Response;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;

public class PreInvocationAuthorizationVoter implements AccessDecisionVoter<MethodInvocation> {

	private final ResponseEvaluator eval;
	private final RequestFactory requestFactory;
	private final PdpClient pdpClient;
	private final Collection<RequestAttributeConverter> attributeConverter;

	public PreInvocationAuthorizationVoter(Collection<RequestAttributeConverter> attributeConverter,
			RequestFactory requestFactory, PdpClient pdpClient, ResponseEvaluator eval) {
		this.attributeConverter = attributeConverter;
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

		for (ConfigAttribute a : attributes) {
			if (isPreInvocationAttribute(a)) {
				RequestAttribute r = (RequestAttribute) a;
				requestAttrs.add(attributeConverter.stream().filter(c -> c.supportsValueType(r.getValue().getClass()))
						.findFirst().map(c -> c.convert(r, authentication, method)).orElse(r));
			}
		}

		if (requestAttrs.isEmpty()) {
			return ACCESS_ABSTAIN;
		}

		Request request = requestFactory.build(requestAttrs);
		Response response = pdpClient.decide(request);
		boolean allowed = eval.evaluateToBoolean(response);

		return allowed ? ACCESS_GRANTED : ACCESS_DENIED;
	}

	private boolean isPreInvocationAttribute(ConfigAttribute attribute) {
		return attribute instanceof PreInvocationAttribute && attribute instanceof RequestAttribute;
	}

}
