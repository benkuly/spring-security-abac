package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;

import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;

public abstract class CollectingSecurityMetadataSource<T> implements SecurityMetadataSource {

	protected Collection<ConfigAttribute> collectConfigAttributes(T context, RequestAttributeProvider<T> collector,
			ConfigAttributeFactory factory) {
		return factory.createConfigAttributes(collector.getAttributes(context));
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return Collections.emptyList();
	}
}
