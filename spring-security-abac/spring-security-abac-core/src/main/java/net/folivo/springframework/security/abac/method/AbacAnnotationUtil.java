package net.folivo.springframework.security.abac.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

public class AbacAnnotationUtil {

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
