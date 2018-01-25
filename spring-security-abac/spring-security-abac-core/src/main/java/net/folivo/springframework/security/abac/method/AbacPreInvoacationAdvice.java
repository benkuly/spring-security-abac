package net.folivo.springframework.security.abac.method;

import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pep.PepEngine;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;

public class AbacPreInvoacationAdvice implements PreInvocationAuthorizationAdvice {

	private final PepEngine<MethodInvocationContext> pep;

	public AbacPreInvoacationAdvice(PepEngine<MethodInvocationContext> pep) {
		this.pep = pep;
	}

	@Override
	public boolean before(Authentication authentication, MethodInvocation mi,
			PreInvocationAttribute preInvocationAttribute) {
		AbacPreInvocationAttribute attr = (AbacPreInvocationAttribute) preInvocationAttribute;
		return pep.buildRequestAndEvaluateToBoolean(attr.getAttributes(),
				new MethodInvocationContext(mi, Optional.empty()));
	}

}
