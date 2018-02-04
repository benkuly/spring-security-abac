package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.prepost.RequestAttributesHolder;

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
		return attribute.getClass().isAssignableFrom(supportedAttribute.getClass());
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.getClass().isAssignableFrom(supportedContext.getClass());
	}

	@Override
	public int vote(Authentication authentication, T object, Collection<ConfigAttribute> attributes) {
		RequestAttributesHolder holder = findRequestAttributeHolder(attributes);
		if (holder != null && pep.buildRequestAndEvaluateToBoolean(holder.getAttributes(), object))
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
