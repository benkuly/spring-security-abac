package net.folivo.springframework.security.abac.attributes;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ProcessorUtils {

	private final static Log log = LogFactory.getLog(ProcessorUtils.class);

	// TODO caching/performance!
	// TODO maybe allow multiple processors for one attribute
	public final static <T> Collection<RequestAttribute> process(Collection<RequestAttribute> attrs, T context,
			Collection<RequestAttributeProcessor<T>> processors) {
		return attrs.stream().flatMap(a -> processors.stream().filter(p -> {
			Object value = a.getValue();
			if (value == null) {
				if (log.isDebugEnabled())
					log.debug("RequestAttribute " + a + " will not be processed because its value is null!");
				return false;
			}
			return p.supports(a);
		}).findFirst().map(p -> p.process(a, context)).orElse(Collections.singleton(a)).stream())
				.collect(Collectors.toList());
	}

}
