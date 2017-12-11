package net.folivo.springframework.security.abac.method;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

import net.folivo.springframework.security.abac.pep.CollectingSecurityMetadataSource;
import net.folivo.springframework.security.abac.pep.ProviderCollector;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;

public class AbacAnnotationMethodSecurityMetadataSource extends CollectingSecurityMetadataSource<MethodInvocation>
		implements MethodSecurityMetadataSource {

	public AbacAnnotationMethodSecurityMetadataSource(Collection<ProviderCollector<MethodInvocation>> collectors) {
		super(collectors);
	}

	// TODO urgs. why do they need that?
	// TODO ! a very very very bad workaournd to say aop: hey there is something.
	@Override
	public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
		boolean preAuthorize = AbacAnnotationUtil.findAnnotation(method, targetClass, AbacPreAuthorize.class) != null;
		boolean postAuthorize = AbacAnnotationUtil.findAnnotation(method, targetClass, AbacPostAuthorize.class) != null;
		if (preAuthorize || postAuthorize) {
			List<ConfigAttribute> dummy = new ArrayList<>();
			dummy.add(new AbacPreInvocationAttribute(Collections.emptyList()));
			return dummy;
		}
		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if (object instanceof MethodInvocation)
			return collectConfigAttributes((MethodInvocation) object);
		throw new IllegalArgumentException("Object must be a non-null MethodInvocation");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return MethodInvocation.class.isAssignableFrom(clazz);
	}
}
