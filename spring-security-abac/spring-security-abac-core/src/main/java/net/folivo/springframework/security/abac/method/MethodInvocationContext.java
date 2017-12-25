package net.folivo.springframework.security.abac.method;

import org.aopalliance.intercept.MethodInvocation;

public class MethodInvocationContext {

	private final MethodInvocation methodInvocation;
	private final Object returnedObject;

	public MethodInvocationContext(MethodInvocation methodInvocation, Object returnedObject) {
		this.methodInvocation = methodInvocation;
		this.returnedObject = returnedObject;
	}

	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}

	public Object getReturnedObject() {
		return returnedObject;
	}

}
