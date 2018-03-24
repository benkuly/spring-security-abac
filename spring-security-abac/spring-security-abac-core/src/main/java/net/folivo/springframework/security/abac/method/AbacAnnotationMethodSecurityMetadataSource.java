package net.folivo.springframework.security.abac.method;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;

import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.prepost.CollectingSecurityMetadataSource;
import net.folivo.springframework.security.abac.prepost.ConfigAttributeFactory;

//TODO this class is so outrageous ugly
public class AbacAnnotationMethodSecurityMetadataSource
		extends CollectingSecurityMetadataSource<MethodInvocationContext> implements SecurityMetadataSource {

	private final RequestAttributeProvider<MethodInvocationContext> preCollector;
	private final RequestAttributeProvider<MethodInvocationContext> postCollector;
	private final ConfigAttributeFactory preAttributeFactory;
	private final ConfigAttributeFactory postAttributeFactory;

	public AbacAnnotationMethodSecurityMetadataSource(RequestAttributeProvider<MethodInvocationContext> preCollector,
			RequestAttributeProvider<MethodInvocationContext> postCollector, ConfigAttributeFactory preAttributeFactory,
			ConfigAttributeFactory postAttributeFactory) {
		this.preCollector = preCollector;
		this.postCollector = postCollector;
		this.preAttributeFactory = preAttributeFactory;
		this.postAttributeFactory = postAttributeFactory;
	}

	// is there a better solution with only one collector or are two better?
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if (object instanceof MethodInvocationContext) {
			MethodInvocationContext context = (MethodInvocationContext) object;
			Collection<ConfigAttribute> attrs = new ArrayList<>();
			if (context.getPreAuthorize().isPresent()) {
				attrs.addAll(collectConfigAttributes(context, preCollector, preAttributeFactory));
			}
			if (context.getPostAuthorize().isPresent()) {
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
