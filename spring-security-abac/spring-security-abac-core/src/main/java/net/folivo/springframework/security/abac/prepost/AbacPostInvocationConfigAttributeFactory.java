package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public class AbacPostInvocationConfigAttributeFactory implements ConfigAttributeFactory {

	@Override
	public Collection<ConfigAttribute> createConfigAttributes(Stream<RequestAttribute> attrs) {
		return Collections.singleton(new AbacPostInvocationAttribute(attrs));
	}

}
