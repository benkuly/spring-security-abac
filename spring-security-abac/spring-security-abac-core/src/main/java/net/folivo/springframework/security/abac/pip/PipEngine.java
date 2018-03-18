package net.folivo.springframework.security.abac.pip;

import java.util.stream.Stream;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;

public interface PipEngine<T> {

	Stream<RequestAttribute> attributeQuery(T context, Stream<RequestAttributeMetadata> metadata);

}
