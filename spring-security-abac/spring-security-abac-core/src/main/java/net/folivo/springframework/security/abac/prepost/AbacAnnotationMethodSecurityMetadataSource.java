package net.folivo.springframework.security.abac.prepost;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.core.context.SecurityContextHolder;

import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;

public class AbacAnnotationMethodSecurityMetadataSource implements MethodSecurityMetadataSource {

	private final Collection<RequestAttributeProvider<MethodInvocationContext>> provider;

	public AbacAnnotationMethodSecurityMetadataSource(
			Collection<RequestAttributeProvider<MethodInvocationContext>> provider) {
		this.provider = provider;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	// TODO is needed (look in Configuration file), but then the context can't be
	// filled. urgs :(
	@Override
	public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {

		return null;
	}

	public Collection<ConfigAttribute> getAttributes(MethodInvocation method, Class<?> targetClass) {

		MethodInvocationContext context = new MethodInvocationContext(
				SecurityContextHolder.getContext().getAuthentication(), method, targetClass);
		Map<String, ConfigAttribute> set = new HashMap<>();

		// TODO bad solution because casting and performance (instead of put, sort
		// backwards and use containsKey)
		provider.stream().sorted(AnnotationAwareOrderComparator.INSTANCE)
				.flatMap(p -> p.getAttributes(context).stream()).filter(a -> a instanceof ConfigAttribute)
				.forEach(a -> set.put(a.getId(), (ConfigAttribute) a));

		return set.values();
	}

	// TODO copy paste from AbstractMethodSecurityMetadataSource, but I need
	// MethodInvocation
	@Override
	public final Collection<ConfigAttribute> getAttributes(Object object) {
		if (object instanceof MethodInvocation) {
			MethodInvocation mi = (MethodInvocation) object;
			Object target = mi.getThis();
			Class<?> targetClass = null;

			if (target != null) {
				targetClass = target instanceof Class<?> ? (Class<?>) target
						: AopProxyUtils.ultimateTargetClass(target);
			}
			Collection<ConfigAttribute> attrs = getAttributes(mi, targetClass);
			if (attrs != null && !attrs.isEmpty()) {
				return attrs;
			}
			if (target != null && !(target instanceof Class<?>)) {
				attrs = getAttributes(mi, target.getClass());
			}
			return attrs;
		}

		throw new IllegalArgumentException("Object must be a non-null MethodInvocation");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return (MethodInvocation.class.isAssignableFrom(clazz));
	}
}
