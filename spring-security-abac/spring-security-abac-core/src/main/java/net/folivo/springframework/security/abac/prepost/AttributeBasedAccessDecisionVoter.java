package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pep.PepEngine;

public class AttributeBasedAccessDecisionVoter<T> implements AccessDecisionVoter<T> {

	private final PepEngine<T> pep;
	private final Class<T> supportedContext;
	private final Class<? extends RequestAttributesHolder> supportedAttribute;

	public AttributeBasedAccessDecisionVoter(PepEngine<T> pep, Class<T> supportedContext,
			Class<? extends RequestAttributesHolder> supportedAttribute) {
		this.pep = pep;
		this.supportedContext = supportedContext;
		this.supportedAttribute = supportedAttribute;
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return supportedAttribute.isAssignableFrom(attribute.getClass());
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return supportedContext.isAssignableFrom(clazz);
	}

	@Override
	public int vote(Authentication authentication, T context, Collection<ConfigAttribute> attributes) {
		RequestAttributesHolder holder = findRequestAttributeHolder(attributes);
		if (holder == null)
			return ACCESS_ABSTAIN;
		if (pep.decide(context, holder.getAttributes()).evaluateToBoolean())
			return ACCESS_GRANTED;
		return ACCESS_DENIED;
	}

	private RequestAttributesHolder findRequestAttributeHolder(Collection<ConfigAttribute> attributes) {
		for (ConfigAttribute c : attributes) {
			// TODO I don't like that we have to check something that should be guaranteed
			if (supports(c))
				return (RequestAttributesHolder) c;
		}
		return null;
	}

}
