package net.folivo.springframework.security.abac.pdp;

public class RequestHolderImpl<T> implements RequestHolder {

	private final T request;

	public RequestHolderImpl(T request) {
		this.request = request;
	}

	@Override
	public T getRequest() {
		return request;
	}

}
