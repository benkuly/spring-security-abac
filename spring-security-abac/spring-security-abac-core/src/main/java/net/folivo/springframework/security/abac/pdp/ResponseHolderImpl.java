package net.folivo.springframework.security.abac.pdp;

public class ResponseHolderImpl<T> implements ResponseHolder {

	private final T response;

	public ResponseHolderImpl(T response) {
		this.response = response;
	}

	@Override
	public T getResponse() {
		return response;
	}

}
