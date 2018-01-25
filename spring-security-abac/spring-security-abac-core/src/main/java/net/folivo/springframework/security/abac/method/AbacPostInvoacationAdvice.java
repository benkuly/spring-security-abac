package net.folivo.springframework.security.abac.method;

import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PostInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pep.PepEngine;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationAttribute;

public class AbacPostInvoacationAdvice implements PostInvocationAuthorizationAdvice {

	private final PepEngine<MethodInvocationContext> pep;

	public AbacPostInvoacationAdvice(PepEngine<MethodInvocationContext> pep) {
		this.pep = pep;
	}

	@Override
	public Object after(Authentication authentication, MethodInvocation mi,
			PostInvocationAttribute postInvocationAttribute, Object returnedObject) {
		AbacPostInvocationAttribute attr = (AbacPostInvocationAttribute) postInvocationAttribute;

		// TODO wtf does the ExpressionBasedPostInvocationAdvice. Why he throws an
		// exception. thats dirty. The AccessDecisionManager should do that.
		if (!pep.buildRequestAndEvaluateToBoolean(attr.getAttributes(),
				new MethodInvocationContext(mi, Optional.of(returnedObject))))
			throw new AccessDeniedException("Access is denied");
		return returnedObject;
	}

}
