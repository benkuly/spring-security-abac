package net.folivo.springframework.security.abac.pip;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;

public interface PipClient<T> {

	// TODO maybe ProviderContext?
	Collection<RequestAttribute> resolve(RequestAttributeMetadata meta, T context);

}
