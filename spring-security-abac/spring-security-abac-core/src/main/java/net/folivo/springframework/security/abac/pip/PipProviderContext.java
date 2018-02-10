package net.folivo.springframework.security.abac.pip;

import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;

public interface PipProviderContext<T> {

	T getOriginalContext();

	RequestAttributeMetadata getAttributeMetadata();

}
