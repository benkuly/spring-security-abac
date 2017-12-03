package net.folivo.springframework.security.abac.prepost;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
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

	// TODO urgs. why does they need that?
	@Override
	public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	public Collection<ConfigAttribute> getAttributes(MethodInvocation context) {

		List<RequestAttributeProvider<MethodInvocation>> preProviders = new ArrayList<>();
		List<RequestAttributeProvider<MethodInvocation>> postProviders = new ArrayList<>();

		// should this be in PepEngine or here?
		providers.sort(AnnotationAwareOrderComparator.INSTANCE);

		for (RequestAttributeProvider<MethodInvocation> p : providers) {
			if (p instanceof AbacAnnotationPreRequestAttributeProvider) {
				preProviders.add(p);
			} else if (p instanceof AbacAnnotationPostRequestAttributeProvider) {
				postProviders.add(p);
			} else {
				// TODO performance: adding to two lists vs add later an sort
				preProviders.add(p);
				postProviders.add(p);
			}
		}

		List<ConfigAttribute> configAttrs = new ArrayList<>(2);
		configAttrs.add(configAttributesFactory.createPreInvocationAttributes(mergeAttribtes(preProviders, context)));
		configAttrs.add(configAttributesFactory.createPostInvocationAttributes(mergeAttribtes(postProviders, context)));

		return configAttrs;
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
}
