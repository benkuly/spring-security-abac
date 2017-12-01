package net.folivo.springframework.security.abac.prepost;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pep.PepEngine;

public class AbacPreInvoacationAdvice implements PreInvocationAuthorizationAdvice {

	private final PepEngine<MethodInvocation> engine;

	public AbacPreInvoacationAdvice(PepEngine<MethodInvocation> engine) {
		this.engine = engine;
	}

	@Override
	public boolean before(Authentication authentication, MethodInvocation mi,
			PreInvocationAttribute preInvocationAttribute) {
		AbacPreInvocationAttribute attr = (AbacPreInvocationAttribute) preInvocationAttribute;

		return engine.buildRequestAndEvaluateToBoolean(attr.getAttributes());
	}

}
