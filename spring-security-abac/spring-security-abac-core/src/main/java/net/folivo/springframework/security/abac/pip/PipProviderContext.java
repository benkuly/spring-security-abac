package net.folivo.springframework.security.abac.pip;

import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;

public class PipProviderContext<T> {

	private final T originalContext;
	private final RequestAttributeMetadata attributeMetadata;

	public PipProviderContext(T originalContext, RequestAttributeMetadata attributeMetadata) {
		this.originalContext = originalContext;
		this.attributeMetadata = attributeMetadata;
	}

	public T getOriginalContext() {
		return originalContext;
	}

	public RequestAttributeMetadata getAttributeMetadata() {
		return attributeMetadata;
	}

}
