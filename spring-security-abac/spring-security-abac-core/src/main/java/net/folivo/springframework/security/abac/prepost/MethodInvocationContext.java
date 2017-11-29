package net.folivo.springframework.security.abac.prepost;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;

public class MethodInvocationContext {

	private final Authentication authentication;
	private final MethodInvocation methodInvocation;
	private final Class<?> targetClass;

	public MethodInvocationContext(Authentication authentication, MethodInvocation methodInvocation,
			Class<?> targetClass) {
		this.authentication = authentication;
		this.methodInvocation = methodInvocation;
		this.targetClass = targetClass;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

}
