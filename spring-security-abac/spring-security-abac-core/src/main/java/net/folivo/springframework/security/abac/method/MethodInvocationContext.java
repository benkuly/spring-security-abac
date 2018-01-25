package net.folivo.springframework.security.abac.method;

import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;

public class MethodInvocationContext {

	private final MethodInvocation methodInvocation;
	private final Optional<Object> returnedObject;

	public MethodInvocationContext(MethodInvocation methodInvocation, Optional<Object> returnedObject) {
		this.methodInvocation = methodInvocation;
		this.returnedObject = returnedObject;
	}

	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}

	public Optional<Object> getReturnedObject() {
		return returnedObject;
	}

}
