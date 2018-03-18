package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;
import java.util.stream.Stream;

public final class ProcessorUtils {

	// TODO caching/performance!
	// TODO maybe allow multiple processors for one attribute
	public final static <T> Stream<RequestAttribute> process(Stream<RequestAttribute> attrs, T context,
			Collection<RequestAttributeProcessor<T>> processors) {
		return attrs.flatMap(a -> processors.stream().filter(p -> p.supports(a)).findFirst()
				.map(p -> p.process(a, context)).orElse(Stream.of(a)));
	}
}
