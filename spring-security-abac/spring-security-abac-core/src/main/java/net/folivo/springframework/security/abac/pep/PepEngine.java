package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

//TODO mention, that usually Pep collects the attributes
public interface PepEngine<T> {

	PepResponse decide(T context, Collection<RequestAttribute> attributes);

	PepResponse decide(T context);

}
