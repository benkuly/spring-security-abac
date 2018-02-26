package net.folivo.springframework.security.abac.pip;

import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;

public interface PipEngine<T> {

	Collection<RequestAttribute> attributeQuery(T context, Collection<RequestAttributeMetadata> metadata);

}
