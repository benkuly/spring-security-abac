package net.folivo.springframework.security.abac.method;

import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;

public class MethodInvocationContext {

	private final MethodInvocation methodInvocation;
	private Optional<Object> returnedObject;
	private final Optional<AbacPreAuthorize> preAuthorize;
	private final Optional<AbacPostAuthorize> postAuthorize;

	public MethodInvocationContext(MethodInvocation methodInvocation, Optional<Object> returnedObject,
			Optional<AbacPreAuthorize> preAuthorize, Optional<AbacPostAuthorize> postAuthorize) {
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

	public Optional<AbacPreAuthorize> getPreAuthorize() {
		return preAuthorize;
	}

	public Optional<AbacPostAuthorize> getPostAuthorize() {
		return postAuthorize;
	}

}
