package net.folivo.springframework.security.abac.pdp;

import java.util.Collection;

public interface RequestFactory<R> {

	R build(Collection<RequestAttribute> requestAttrs);

}
