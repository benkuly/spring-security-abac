package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public interface ConfigAttributeFactory {

	Collection<ConfigAttribute> createConfigAttributes(Stream<RequestAttribute> attrs);

}
