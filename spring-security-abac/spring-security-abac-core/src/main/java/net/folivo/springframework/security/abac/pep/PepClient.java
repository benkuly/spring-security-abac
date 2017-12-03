package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public interface PepClient<T> {

	boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes, T context);

}
