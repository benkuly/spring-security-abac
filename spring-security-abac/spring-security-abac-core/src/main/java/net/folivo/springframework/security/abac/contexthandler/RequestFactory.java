package net.folivo.springframework.security.abac.contexthandler;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public interface RequestFactory<R> {

	R build(Collection<RequestAttribute> requestAttrs);

}
