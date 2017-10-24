package net.folivo.springframework.security.abac.pdp;

public interface ResponseEvaluator {

	boolean evaluateToBoolean(Response response);

}
