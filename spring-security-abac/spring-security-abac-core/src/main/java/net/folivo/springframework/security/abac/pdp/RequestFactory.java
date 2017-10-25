package net.folivo.springframework.security.abac.pdp;

import java.util.Collection;

public interface RequestFactory {

	Request build(Collection<RequestAttribute> requestAttrs);

}
