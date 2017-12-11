package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.access.ConfigAttribute;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pep.ConfigAttributeFactory;

public class AbacPreInvocationConfigAttributeFactory implements ConfigAttributeFactory {

	@Override
	public Collection<ConfigAttribute> createConfigAttributes(Collection<RequestAttribute> attrs) {
		return Collections.singleton(new AbacPreInvocationAttribute(attrs));
	}

}
