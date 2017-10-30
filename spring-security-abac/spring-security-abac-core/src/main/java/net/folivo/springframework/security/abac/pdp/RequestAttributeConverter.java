package net.folivo.springframework.security.abac.pdp;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;

public interface RequestAttributeConverter {

	boolean supportsValueType(Class<?> clazz);

	PdpRequestAttribute convert(PdpRequestAttribute requestAttr, Authentication authentication, MethodInvocation method);

}
