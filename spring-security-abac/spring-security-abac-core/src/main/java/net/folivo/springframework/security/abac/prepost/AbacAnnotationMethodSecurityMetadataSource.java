package net.folivo.springframework.security.abac.prepost;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.util.ClassUtils;

public class AbacAnnotationMethodSecurityMetadataSource extends AbstractMethodSecurityMetadataSource {

	private final PrePostRequestAttributeFactory<String> attributeFactory;

	public AbacAnnotationMethodSecurityMetadataSource(PrePostRequestAttributeFactory<String> attributeFactory) {
		this.attributeFactory = attributeFactory;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
		if (method.getDeclaringClass() == Object.class) {
			return Collections.emptyList();
		}

		logger.trace("Looking for Abac Pre/Post annotations for method '" + method.getName() + "' on target class '"
				+ targetClass + "'");
		AbacPreAuthorize abacPreAuthorize = findAnnotation(method, targetClass, AbacPreAuthorize.class);
		// TODO: Can we check for void methods and throw an exception here?
		AbacPostAuthorize abacPostAuthorize = findAnnotation(method, targetClass, AbacPostAuthorize.class);

		if (abacPreAuthorize == null && abacPostAuthorize == null) {
			// There is no meta-data so return
			logger.trace("No abac annotations found");
			return Collections.emptyList();
		}

		ArrayList<ConfigAttribute> attrs = new ArrayList<>();

		if (abacPreAuthorize != null) {
			createAndAddPreInvocationAttribute(AttributeCategory.SUBJECT, abacPreAuthorize.subjectAttributes(), attrs);
			createAndAddPreInvocationAttribute(AttributeCategory.RESOURCE, abacPreAuthorize.resourceAttributes(),
					attrs);
			createAndAddPreInvocationAttribute(AttributeCategory.ACTION, abacPreAuthorize.actionAttributes(), attrs);
			createAndAddPreInvocationAttribute(AttributeCategory.ENVIRONMENT, abacPreAuthorize.environmentAttributes(),
					attrs);
		}

		if (abacPostAuthorize != null) {
			createAndAddPostInvocationAttribute(AttributeCategory.SUBJECT, abacPostAuthorize.subjectAttributes(),
					attrs);
			createAndAddPostInvocationAttribute(AttributeCategory.RESOURCE, abacPostAuthorize.resourceAttributes(),
					attrs);
			createAndAddPostInvocationAttribute(AttributeCategory.ACTION, abacPostAuthorize.actionAttributes(), attrs);
			createAndAddPostInvocationAttribute(AttributeCategory.ENVIRONMENT,
					abacPostAuthorize.environmentAttributes(), attrs);
		}

		attrs.trimToSize();

		return attrs;
	}

	private void createAndAddPreInvocationAttribute(AttributeCategory category, AttributeMapping[] attributes,
			List<ConfigAttribute> listToAdd) {
		Arrays.asList(attributes).stream()
				.map(a -> attributeFactory.createPreInvocationAttributes(AttributeCategory.SUBJECT, a.id(), a.value()))
				.forEach(listToAdd::add);
	}

	private void createAndAddPostInvocationAttribute(AttributeCategory category, AttributeMapping[] attributes,
			List<ConfigAttribute> listToAdd) {
		Arrays.asList(attributes).stream()
				.map(a -> attributeFactory.createPostInvocationAttributes(AttributeCategory.SUBJECT, a.id(), a.value()))
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
	private <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationClass) {
		// The method may be on an interface, but we need attributes from the target
		// class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
		A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationClass);

		if (annotation != null) {
			logger.debug(annotation + " found on specific method: " + specificMethod);
			return annotation;
		}

		// Check the original (e.g. interface) method
		if (specificMethod != method) {
			annotation = AnnotationUtils.findAnnotation(method, annotationClass);

			if (annotation != null) {
				logger.debug(annotation + " found on: " + method);
				return annotation;
			}
		}

		// Check the class-level (note declaringClass, not targetClass, which may not
		// actually implement the method)
		annotation = AnnotationUtils.findAnnotation(specificMethod.getDeclaringClass(), annotationClass);

		if (annotation != null) {
			logger.debug(annotation + " found on: " + specificMethod.getDeclaringClass().getName());
			return annotation;
		}

		return null;
	}

}
