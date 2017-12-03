package net.folivo.springframework.security.abac.pdp;

public interface ResponseEvaluator<S> {

	boolean evaluateToBoolean(S response);

}
