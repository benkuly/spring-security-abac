package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.pep.ConfigAttributeFactory;

public class AbacPostInvocationConfigAttributeFactory implements ConfigAttributeFactory {

	@Override
	public Collection<ConfigAttribute> createConfigAttributes(Collection<RequestAttribute> attrs) {
		return Collections.singleton(new AbacPostInvocationAttribute(attrs));
	}

}
