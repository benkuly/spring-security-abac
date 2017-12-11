package net.folivo.springframework.security.abac.pep;

import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;

public interface ProviderCollector<T> {

	Collection<ConfigAttribute> collect(T context);

}
