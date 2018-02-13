package net.folivo.springframework.security.abac.pip;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

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
	public Collection<RequestAttribute> resolve(T context, Collection<RequestAttributeMetadata> metadata) {
		return metadata.stream().map(metadatum -> {
			PipProviderContext<T> providerContext = new PipProviderContext<>(context, metadatum);
			Optional<RequestAttribute> attr = collector.collectFirst(providerContext);
			if (log.isDebugEnabled() && attr.isPresent())
				log.debug("Cannot find any attribute for metadata:" + metadata);
			return attr;
		}).flatMap(Optional::stream).collect(Collectors.toList());
	}
}
