package net.folivo.springframework.security.abac.pip;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.ProviderCollector;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;
import net.folivo.springframework.security.abac.context.RequestContextMapper;

public class ContextMappingPipEngine<T> extends StandardPipEngine<T> {

	private final RequestContextMapper<T> mapper;

	public ContextMappingPipEngine(ProviderCollector<PipProviderContext<T>> collector, RequestContextMapper<T> mapper) {
		super(collector);
		this.mapper = mapper;
	}

	public Collection<RequestAttribute> attributeQuery(String requestIdentifier,
			Collection<RequestAttributeMetadata> metadata) {
		T context = mapper.resolve(requestIdentifier);
		return attributeQuery(context, metadata);
	}
}
