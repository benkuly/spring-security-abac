package net.folivo.springframework.security.abac.method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pep.PepClient;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;

public class AbacPreInvoacationAdvice implements PreInvocationAuthorizationAdvice {

	private final PepClient<MethodInvocation> pep;

	public AbacPreInvoacationAdvice(PepClient<MethodInvocation> pep) {
		this.pep = pep;
	}

	@Override
	public boolean before(Authentication authentication, MethodInvocation mi,
			PreInvocationAttribute preInvocationAttribute) {
		AbacPreInvocationAttribute attr = (AbacPreInvocationAttribute) preInvocationAttribute;
		return pep.buildRequestAndEvaluateToBoolean(attr.getAttributes(), mi);
	}

}
