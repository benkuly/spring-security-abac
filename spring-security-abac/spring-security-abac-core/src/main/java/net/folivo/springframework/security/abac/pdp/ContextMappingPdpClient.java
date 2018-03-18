package net.folivo.springframework.security.abac.pdp;

import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.context.RequestContextMapper;
import net.folivo.springframework.security.abac.pep.PepResponse;

public abstract class ContextMappingPdpClient<T> implements PdpClient<T> {

	private final RequestContextMapper<T> mapper;
	private final RequestAttributeFactory attributeFactory;

	public ContextMappingPdpClient(RequestContextMapper<T> mapper, RequestAttributeFactory attributeFactory) {
		this.mapper = mapper;
		this.attributeFactory = attributeFactory;
	}

	@Override
	public final PepResponse decide(T context, Stream<RequestAttribute> attrs) {
		String identifier = mapper.map(context);
		Stream<RequestAttribute> attributesPlusContext = Streams.concat(attrs, Stream
				.of(attributeFactory.build(RequestContextMapper.ATTR_CAT, RequestContextMapper.ATTR_ID, identifier)));
		PepResponse response = decide(attributesPlusContext);
		mapper.release(identifier);
		return response;
	}

	protected abstract PepResponse decide(Stream<RequestAttribute> attrs);

}
