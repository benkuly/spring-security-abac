package net.folivo.springframework.security.abac.xacml.core.zold;

import org.springframework.security.core.Authentication;

import com.att.research.xacml.api.RequestAttributes;

//TODO testen
public interface ResourceAttributeProvider {

	public boolean supports(Class<?> clazz);

	public RequestAttributes getAttributes(Authentication authentication, Object resource, String action);

}
