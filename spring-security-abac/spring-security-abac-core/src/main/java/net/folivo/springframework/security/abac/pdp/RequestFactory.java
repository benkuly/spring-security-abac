package net.folivo.springframework.security.abac.pdp;

import java.util.Collection;

public interface RequestFactory {

	PdpRequest build(Collection<PdpRequestAttribute> requestAttrs);

}
