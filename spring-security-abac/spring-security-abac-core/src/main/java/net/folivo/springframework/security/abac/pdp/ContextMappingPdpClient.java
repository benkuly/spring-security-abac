package net.folivo.springframework.security.abac.pdp;

import java.util.Collection;

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
	public final PepResponse decide(T context, Collection<RequestAttribute> attrs) {
		String identifier = mapper.map(context);
		attrs.add(attributeFactory.build(RequestContextMapper.ATTR_CAT, RequestContextMapper.ATTR_ID, identifier));
		PepResponse response = decide(attrs);
		mapper.release(identifier);
		return response;
	}

	protected abstract PepResponse decide(Collection<RequestAttribute> attrs);

}
