package net.folivo.springframework.security.abac.method;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

import net.folivo.springframework.security.abac.pep.CollectingSecurityMetadataSource;
import net.folivo.springframework.security.abac.pep.ConfigAttributeFactory;
import net.folivo.springframework.security.abac.pep.ProviderCollector;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;

//TODO this class is so outrageous ugly
public class AbacAnnotationMethodSecurityMetadataSource
		extends CollectingSecurityMetadataSource<MethodInvocationContext> implements MethodSecurityMetadataSource {

	private final ProviderCollector<MethodInvocationContext> preCollector;
	private final ProviderCollector<MethodInvocationContext> postCollector;
	private final ConfigAttributeFactory preAttributeFactory;
	private final ConfigAttributeFactory postAttributeFactory;

	public AbacAnnotationMethodSecurityMetadataSource(ProviderCollector<MethodInvocationContext> preCollector,
			ProviderCollector<MethodInvocationContext> postCollector, ConfigAttributeFactory preAttributeFactory,
			ConfigAttributeFactory postAttributeFactory) {
		this.preCollector = preCollector;
		this.postCollector = postCollector;
		this.preAttributeFactory = preAttributeFactory;
		this.postAttributeFactory = postAttributeFactory;
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

	// is there a better solution?
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if (object instanceof MethodInvocationContext) {
			MethodInvocationContext context = (MethodInvocationContext) object;
			Collection<ConfigAttribute> attrs = new ArrayList<>();
			if (AbacAnnotationUtil.findAnnotation(context.getMethodInvocation(), AbacPreAuthorize.class) != null) {
				attrs.addAll(collectConfigAttributes(context, preCollector, preAttributeFactory));
			}
			if (AbacAnnotationUtil.findAnnotation(context.getMethodInvocation(), AbacPostAuthorize.class) != null) {
				attrs.addAll(collectConfigAttributes(context, postCollector, postAttributeFactory));
			}
			return attrs;
		}
		throw new IllegalArgumentException("Object must be a non-null MethodInvocationContext");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return MethodInvocationContext.class.isAssignableFrom(clazz);
	}
}
