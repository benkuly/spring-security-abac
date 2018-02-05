package net.folivo.springframework.security.abac.method;

import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;

public class MethodInvocationContext {

	private final MethodInvocation methodInvocation;
	private Optional<Object> returnedObject;
	private final boolean preAuthorize;
	private final boolean postAuthorize;

	public MethodInvocationContext(MethodInvocation methodInvocation, Optional<Object> returnedObject,
			boolean preAuthorize, boolean postAuthorize) {
		this.methodInvocation = methodInvocation;
		this.returnedObject = returnedObject;
		this.preAuthorize = preAuthorize;
		this.postAuthorize = postAuthorize;
	}

	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}

	public Optional<Object> getReturnedObject() {
		return returnedObject;
	}

	public void setReturnedObject(Optional<Object> returnedObject) {
		this.returnedObject = returnedObject;
	}

	public boolean isPreAuthorize() {
		return preAuthorize;
	}

	public boolean isPostAuthorize() {
		return postAuthorize;
	}

}
