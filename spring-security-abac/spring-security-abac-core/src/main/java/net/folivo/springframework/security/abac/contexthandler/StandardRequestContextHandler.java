package net.folivo.springframework.security.abac.contexthandler;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.pep.PepResponse;
import net.folivo.springframework.security.abac.pep.PepResponseFactory;

public class StandardRequestContextHandler<R, S, T> implements RequestContextHandler<T> {

	private final PdpClient<R, S, T> pdp;
	private final PdpRequestFactory<R> requestFactory;
	private final PepResponseFactory<S> responseFactory;

	public StandardRequestContextHandler(PdpClient<R, S, T> pdp, PdpRequestFactory<R> requestFactory,
			PepResponseFactory<S> responseFactory) {
		this.pdp = pdp;
		this.requestFactory = requestFactory;
		this.responseFactory = responseFactory;
	}

	@Override
	public PepResponse decide(T context, Collection<RequestAttribute> attrs) {
		R request = requestFactory.build(attrs);
		S response = pdp.decide(request, context);
		return responseFactory.build(response);
	}
}
