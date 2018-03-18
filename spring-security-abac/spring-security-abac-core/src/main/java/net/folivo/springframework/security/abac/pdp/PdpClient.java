package net.folivo.springframework.security.abac.pdp;

import java.util.stream.Stream;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.pep.PepResponse;

public interface PdpClient<T> {

	PepResponse decide(T context, Stream<RequestAttribute> attrs);

}
