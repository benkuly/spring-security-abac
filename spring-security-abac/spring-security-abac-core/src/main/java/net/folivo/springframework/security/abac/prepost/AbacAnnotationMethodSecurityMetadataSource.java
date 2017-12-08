package net.folivo.springframework.security.abac.prepost;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;

public class AbacAnnotationMethodSecurityMetadataSource implements MethodSecurityMetadataSource {

	private final List<RequestAttributeProvider<MethodInvocation>> providers;
	private final PrePostInvocationAttributeFactory configAttributesFactory;

	public AbacAnnotationMethodSecurityMetadataSource(List<RequestAttributeProvider<MethodInvocation>> providers,
			PrePostInvocationAttributeFactory configAttributesFactory) {
		this.providers = providers;
		this.configAttributesFactory = configAttributesFactory;
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
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return Collections.emptyList();
	}

	public Collection<ConfigAttribute> getAttributes(MethodInvocation context) {
		// TODO why? it's from prepost source
		if (context.getMethod().getDeclaringClass() == Object.class) {
			return Collections.emptyList();
		}

		// TODO better check if annotations are present! this way is very inefficient
		// and only a workaroud
		boolean preAuthorize = hasAbacAnnotation(context, AbacPreAuthorize.class);
		boolean postAuthorize = hasAbacAnnotation(context, AbacPostAuthorize.class);
		if (preAuthorize || postAuthorize) {

			List<RequestAttributeProvider<MethodInvocation>> preProviders = new ArrayList<>();
			List<RequestAttributeProvider<MethodInvocation>> postProviders = new ArrayList<>();

			// should this be in PepEngine or here?
			providers.sort(AnnotationAwareOrderComparator.INSTANCE);

			for (RequestAttributeProvider<MethodInvocation> p : providers) {
				if (p instanceof AbacAnnotationPreRequestAttributeProvider && preAuthorize) {
					preProviders.add(p);
				} else if (p instanceof AbacAnnotationPostRequestAttributeProvider && postAuthorize) {
					postProviders.add(p);
				} else {
					// TODO performance: adding to two lists vs add later an sort
					if (preAuthorize)
						preProviders.add(p);
					if (postAuthorize)
						postProviders.add(p);
				}
			}

			List<ConfigAttribute> configAttrs = new ArrayList<>(2);
			if (preAuthorize)
				configAttrs.add(
						configAttributesFactory.createPreInvocationAttributes(mergeAttribtes(preProviders, context)));
			if (postAuthorize)
				configAttrs.add(
						configAttributesFactory.createPostInvocationAttributes(mergeAttribtes(postProviders, context)));

			return configAttrs;
		}
		return Collections.emptyList();
	}

	private Collection<RequestAttribute> mergeAttribtes(List<RequestAttributeProvider<MethodInvocation>> ps,
			MethodInvocation context) {
		// TODO bad solution because casting and performance (instead of put, sort
		// backwards and use containsKey)
		// TODO maybe in PepEngine?
		Map<String, RequestAttribute> attrs = new HashMap<>();
		ps.stream().flatMap(p -> p.getAttributes(context).stream()).forEach(a -> attrs.put(a.getId(), a));
		return attrs.values();
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return (MethodInvocation.class.isAssignableFrom(clazz));
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if (object instanceof MethodInvocation)
			return getAttributes((MethodInvocation) object);
		throw new IllegalArgumentException("Object must be a non-null MethodInvocation");
	}

	// TODO baaaaad, why not AnnotationUtils
	private <A extends Annotation> boolean hasAbacAnnotation(MethodInvocation mi, Class<A> annotation) {
		Object target = mi.getThis();
		Class<?> targetClass = null;

		if (target != null) {
			targetClass = target instanceof Class<?> ? (Class<?>) target : AopProxyUtils.ultimateTargetClass(target);
		}
		boolean hasAnnotation = AbacAnnotationUtil.findAnnotation(mi.getMethod(), targetClass, annotation) != null;
		if (hasAnnotation) {
			return true;
		}
		if (target != null && !(target instanceof Class<?>)) {
			hasAnnotation = AbacAnnotationUtil.findAnnotation(mi.getMethod(), target.getClass(), annotation) != null;
		}
		return hasAnnotation;
	}
}
