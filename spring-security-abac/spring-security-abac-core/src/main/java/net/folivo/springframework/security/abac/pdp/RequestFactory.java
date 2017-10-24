package net.folivo.springframework.security.abac.pdp;

import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;

public interface RequestFactory {

	Request build(Collection<ConfigAttribute> requestAttrs);

}
