package net.folivo.springframework.security.abac.pip;

import java.util.stream.Stream;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.context.RequestContextMapper;

public class ContextMappingPipEngine<T> extends StandardPipEngine<T> {

	private final RequestContextMapper<T> mapper;

	public ContextMappingPipEngine(RequestAttributeProvider<PipProviderContext<T>> collector,
			RequestContextMapper<T> mapper) {
		super(collector);
		this.mapper = mapper;
	}

	public Stream<RequestAttribute> attributeQuery(String requestIdentifier,
			Stream<RequestAttributeMetadata> metadata) {
		T context = mapper.resolve(requestIdentifier);
		return attributeQuery(context, metadata);
	}
}
