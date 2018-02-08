package net.folivo.springframework.security.abac.pep;

public interface PepResponseFactory<S> {

	PepResponse build(S originalResponse);

}
