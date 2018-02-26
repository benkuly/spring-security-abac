package net.folivo.springframework.security.abac.pdp;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.pep.PepResponse;

public interface PdpClient<T> {

	PepResponse decide(T context, Collection<RequestAttribute> attrs);

}
