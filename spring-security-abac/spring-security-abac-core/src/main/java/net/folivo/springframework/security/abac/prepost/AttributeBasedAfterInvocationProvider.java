package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pep.PepEngine;

public class AttributeBasedAfterInvocationProvider<T> implements AfterInvocationProvider {

	private final PepEngine<T> pep;
	private final Class<T> supportedContext;
	private final Class<? extends RequestAttributesHolder> supportedAttribute;

	public AttributeBasedAfterInvocationProvider(PepEngine<T> pep, Class<T> supportedContext,
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

	// TODO urgs
	@SuppressWarnings("unchecked")
	@Override
	public Object decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes,
			Object returnedObject) throws AccessDeniedException {
		RequestAttributesHolder holder = findRequestAttributeHolder(attributes);
		if (holder != null
				&& !pep.decide((T) object, holder.getAttributes()).evaluateToBoolean())
			throw new AccessDeniedException("Access is denied");
		return returnedObject;
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
