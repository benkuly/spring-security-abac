package net.folivo.springframework.security.abac.pdp;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;

public interface RequestAttributeConverter {

	boolean supportsValueType(Class<?> clazz);

	RequestAttribute convert(RequestAttribute requestAttr, Authentication authentication, MethodInvocation method);

}
