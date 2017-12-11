package net.folivo.springframework.security.abac.pep;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;

//TODO i don't like that
public abstract class CollectingSecurityMetadataSource<T> implements SecurityMetadataSource {

	private final Collection<ProviderCollector<T>> collectors;

	public CollectingSecurityMetadataSource(Collection<ProviderCollector<T>> collectors) {
		this.collectors = collectors;
	}

	public Collection<ConfigAttribute> collectConfigAttributes(T context) {
		return collectors.stream().flatMap(c -> c.collect(context).stream()).collect(Collectors.toList());
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return Collections.emptyList();
	}
}
