package net.folivo.springframework.security.abac.pip;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.folivo.springframework.security.abac.attributes.ProviderCollector;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;

public class StandardPipEngine<T> implements PipEngine<T> {

	private final Log log = LogFactory.getLog(StandardPipEngine.class);
	private final ProviderCollector<PipProviderContext<T>> collector;

	public StandardPipEngine(ProviderCollector<PipProviderContext<T>> collector) {
		this.collector = collector;
	}

	@Override
	public Stream<RequestAttribute> attributeQuery(T context, Stream<RequestAttributeMetadata> metadata) {
		return metadata.map(metadatum -> {
			PipProviderContext<T> providerContext = new PipProviderContext<>(context, metadatum);
			Optional<RequestAttribute> attr = collector.collect(providerContext).findFirst();
			if (log.isDebugEnabled() && attr.isPresent())
				log.debug("Cannot find any attribute for metadata:" + metadata);
			return attr;
		}).flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()/* TODO Java 9: Optional::stream */);
	}
}
