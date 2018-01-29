package net.folivo.springframework.security.abac.contexthandler;

public interface ResponseEvaluator<S> {

	boolean evaluateToBoolean(S response);

}
