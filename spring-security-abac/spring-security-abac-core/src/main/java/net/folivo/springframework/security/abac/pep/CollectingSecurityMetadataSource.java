package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;

import net.folivo.springframework.security.abac.attributes.ProviderCollector;

//TODO i don't like that
public abstract class CollectingSecurityMetadataSource<T> implements SecurityMetadataSource {

	protected Collection<ConfigAttribute> collectConfigAttributes(T context, ProviderCollector<T> collector,
			ConfigAttributeFactory factory) {
		return factory.createConfigAttributes(collector.collect(context));
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return Collections.emptyList();
	}
}
