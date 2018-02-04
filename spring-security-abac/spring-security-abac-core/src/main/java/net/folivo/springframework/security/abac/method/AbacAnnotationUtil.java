package net.folivo.springframework.security.abac.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

public class AbacAnnotationUtil {

	// TODO caching
	// TODO a bit copy paste from AbstractMethodSecurityMetadataSource
	// TODO better name
	public static <T> Collection<T> callMethod(BiFunction<Method, Class<?>, Collection<T>> function,
			MethodInvocation mi) {
		// TODO why? it's from prepost source
		if (mi.getMethod().getDeclaringClass() == Object.class) {
			// TODO logging?
			return Collections.emptyList();
		}

		Object target = mi.getThis();
		Class<?> targetClass = null;

		if (target != null) {
			targetClass = target instanceof Class<?> ? (Class<?>) target : AopProxyUtils.ultimateTargetClass(target);
		}
		Collection<T> attrs = function.apply(mi.getMethod(), targetClass);
		if (attrs != null && !attrs.isEmpty()) {
			return attrs;
		}
		if (target != null && !(target instanceof Class<?>)) {
			attrs = function.apply(mi.getMethod(), target.getClass());
		}
		return attrs;
	}

	public static <A extends Annotation> A findAnnotation(MethodInvocation mi, Class<A> annotationClass) {
		if (mi.getMethod().getDeclaringClass() == Object.class) {
			return null;
		}

		Object target = mi.getThis();
		Class<?> targetClass = null;

		if (target != null) {
			targetClass = target instanceof Class<?> ? (Class<?>) target : AopProxyUtils.ultimateTargetClass(target);
		}
		A annotation = findAnnotation(mi.getMethod(), targetClass, annotationClass);
		if (annotation != null) {
			return annotation;
		}
		if (target != null && !(target instanceof Class<?>)) {
			annotation = findAnnotation(mi.getMethod(), target.getClass(), annotationClass);
		}
		return annotation;
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
	public static <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass,
			Class<A> annotationClass) {
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

}
