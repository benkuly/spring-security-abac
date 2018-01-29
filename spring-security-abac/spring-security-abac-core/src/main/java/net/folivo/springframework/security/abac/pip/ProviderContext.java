package net.folivo.springframework.security.abac.pip;

import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;

public interface ProviderContext<T> {

	T getOriginalContext();

	RequestAttributeMetadata getAttributeMetadata();

}
