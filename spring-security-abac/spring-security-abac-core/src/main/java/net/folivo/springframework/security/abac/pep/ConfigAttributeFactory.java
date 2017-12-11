package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public interface ConfigAttributeFactory {

	Collection<ConfigAttribute> createConfigAttributes(Collection<RequestAttribute> attrs);

}
