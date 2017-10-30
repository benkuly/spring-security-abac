package net.folivo.springframework.security.abac.pdp;

import java.util.Collection;

public interface RequestFactory {

	RequestHolder build(Collection<RequestAttribute> requestAttrs);

}
