package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.PdpResponse;

public interface PepEngine<T> {

	PdpResponse buildRequest(Collection<RequestAttribute> attributes);

	boolean buildRequestAndEvaluateToBoolean(Collection<RequestAttribute> attributes);

}
