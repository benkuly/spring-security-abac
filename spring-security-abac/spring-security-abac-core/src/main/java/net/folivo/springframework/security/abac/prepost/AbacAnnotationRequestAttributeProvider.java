package net.folivo.springframework.security.abac.prepost;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;
import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;

public abstract class AbacAnnotationRequestAttributeProvider implements RequestAttributeProvider<MethodInvocation> {

	private final RequestAttributeFactory requestAttributeFactory;

	public AbacAnnotationRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		this.requestAttributeFactory = requestAttributeFactory;
	}

	protected abstract Collection<RequestAttribute> getAttributes(Method method, Class<?> targetClass);

	protected void createAndAddRequestAttribute(AttributeCategory category, AttributeMapping[] attributes,
			List<RequestAttribute> listToAdd) {
		Arrays.asList(attributes).stream()
				.map(a -> requestAttributeFactory.build(AttributeCategory.SUBJECT, a.id(), a.datatype(), a.value()))
				.forEach(listToAdd::add);
	}

	// TODO the following method is copy paste from
	// org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource
	// it should be outsourced
	/**
	 * See
	 * {@link org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource#getAttributes(Method, Class)}
	 * for the logic of this method. The ordering here is slightly different in that
	 * we consider method-specific annotations on an interface before class-level
	 * ones.
	 */
	protected <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationClass) {
		// The method may be on an interface, but we need attributes from the target
		// class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
		A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationClass);

		if (annotation != null) {
			// logger.debug(annotation + " found on specific method: " + specificMethod);
			return annotation;
		}

		// Check the original (e.g. interface) method
		if (specificMethod != method) {
			annotation = AnnotationUtils.findAnnotation(method, annotationClass);

			if (annotation != null) {
				// logger.debug(annotation + " found on: " + method);
				return annotation;
			}
		}

		// Check the class-level (note declaringClass, not targetClass, which may not
		// actually implement the method)
		annotation = AnnotationUtils.findAnnotation(specificMethod.getDeclaringClass(), annotationClass);

		if (annotation != null) {
			// logger.debug(annotation + " found on: " +
			// specificMethod.getDeclaringClass().getName());
			return annotation;
		}

		return null;
	}

	// TODO a bit copy paste from AbstractMethodSecurityMetadataSource
	@Override
	public Collection<RequestAttribute> getAttributes(MethodInvocation mi) {
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
