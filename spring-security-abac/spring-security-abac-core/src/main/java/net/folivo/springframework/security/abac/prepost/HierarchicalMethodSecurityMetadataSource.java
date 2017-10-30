package net.folivo.springframework.security.abac.prepost;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

import net.folivo.springframework.security.abac.pdp.PdpRequestAttribute;

public class HierarchicalMethodSecurityMetadataSource extends AbstractMethodSecurityMetadataSource {

	private final List<MethodSecurityMetadataSource> metadataSources;

	// TODO find nicer way for order/priority or make clear, that requestAttributes
	// from sources with higher index will override the others if they have same id
	public HierarchicalMethodSecurityMetadataSource(List<MethodSecurityMetadataSource> metadataSources) {
		this.metadataSources = metadataSources;
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
		// TODO this is very dirty (because cast to RequestAttribute)
		// ideas: helper class, generic
		Map<String, ConfigAttribute> set = new HashMap<>();

		metadataSources.stream().flatMap(s -> s.getAttributes(method, targetClass).stream())
				.filter(a -> a instanceof PdpRequestAttribute).forEach(a -> set.put(((PdpRequestAttribute) a).getId(), a));

		return set.values();
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		Set<ConfigAttribute> set = new HashSet<>();
		for (MethodSecurityMetadataSource s : metadataSources) {
			Collection<ConfigAttribute> attrs = s.getAllConfigAttributes();
			if (attrs != null) {
				set.addAll(attrs);
			}
		}
		return set;
	}

}
