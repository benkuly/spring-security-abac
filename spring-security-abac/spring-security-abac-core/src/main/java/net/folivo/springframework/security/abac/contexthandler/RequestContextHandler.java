package net.folivo.springframework.security.abac.contexthandler;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public interface RequestContextHandler<T> {

	boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes, T context);

}
