package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public interface PepEngine {

	boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes);

}
