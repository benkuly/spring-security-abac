package net.folivo.springframework.security.abac.method;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;
import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;

//TODO implements aop needed?
//TODO caching
public abstract class AbacAnnotationRequestAttributeProvider
		implements RequestAttributeProvider<MethodInvocation>, AopInfrastructureBean {

	private final RequestAttributeFactory requestAttributeFactory;

	public AbacAnnotationRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		this.requestAttributeFactory = requestAttributeFactory;
	}

	protected abstract Collection<RequestAttribute> getAttributes(Method method, Class<?> targetClass);

	protected void createAndAddRequestAttribute(AttributeCategory category, AttributeMapping[] attributes,
			List<RequestAttribute> listToAdd) {
		Arrays.asList(attributes).stream()
				.map(a -> requestAttributeFactory.build(category, a.id(), a.datatype(), a.value()))
				.forEach(listToAdd::add);
	}

	// TODO caching
	// TODO a bit copy paste from AbstractMethodSecurityMetadataSource
	@Override
	public Collection<RequestAttribute> getAttributes(MethodInvocation mi) {
		// TODO why? it's from prepost source
		if (mi.getMethod().getDeclaringClass() == Object.class) {
			return Collections.emptyList();
		}

		Object target = mi.getThis();
		Class<?> targetClass = null;

		if (target != null) {
			targetClass = target instanceof Class<?> ? (Class<?>) target : AopProxyUtils.ultimateTargetClass(target);
		}
		Collection<RequestAttribute> attrs = getAttributes(mi.getMethod(), targetClass);
		if (attrs != null && !attrs.isEmpty()) {
			return attrs;
		}
		if (target != null && !(target instanceof Class<?>)) {
			attrs = getAttributes(mi.getMethod(), target.getClass());
		}
		return attrs;
	}

}
