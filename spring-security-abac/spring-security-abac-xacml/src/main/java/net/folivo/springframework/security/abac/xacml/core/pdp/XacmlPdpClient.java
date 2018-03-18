package net.folivo.springframework.security.abac.xacml.core.pdp;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.AttributeFqns;
import org.ow2.authzforce.core.pdp.api.DecisionRequest;
import org.ow2.authzforce.core.pdp.api.DecisionRequestBuilder;
import org.ow2.authzforce.core.pdp.api.DecisionResult;
import org.ow2.authzforce.core.pdp.api.PdpEngine;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.core.pdp.api.value.StringValue;
import org.ow2.authzforce.core.pdp.impl.value.StandardAttributeValueFactories;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;
import net.folivo.springframework.security.abac.context.RequestContextMapper;
import net.folivo.springframework.security.abac.pdp.ContextMappingPdpClient;
import net.folivo.springframework.security.abac.pep.PepResponse;
import net.folivo.springframework.security.abac.xacml.core.config.AbacUtils;

public class XacmlPdpClient<T> extends ContextMappingPdpClient<T> {

	private static final Log log = LogFactory.getLog(XacmlPdpClient.class);
	private final PdpEngine pdp;
	private static final AttributeValueFactoryRegistry ATT_VALUE_FACTORIES = StandardAttributeValueFactories
			.getRegistry(false, Optional.empty());

	public XacmlPdpClient(PdpEngine pdp, RequestContextMapper<T> mapper, RequestAttributeFactory attributeFactory) {
		super(mapper, attributeFactory);
		this.pdp = pdp;
	}

	@Override
	protected PepResponse decide(Stream<RequestAttribute> attrs) {
		DecisionRequest request = buildRequest(attrs);
		DecisionResult response = pdp.evaluate(request);
		return new XacmlPepResponse(response);
	}

	// TODO throw exception when build didn't work
	public DecisionRequest buildRequest(Stream<RequestAttribute> streamAttrs) {

		Collection<RequestAttribute> requestAttrs = streamAttrs.collect(Collectors.toList());

		final DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(-1, requestAttrs.size());

		for (RequestAttribute r : requestAttrs) {
			final RequestAttributeMetadata meta = r.getMetadata();
			if (r.getValue() != null) {
				// TODO maybe use identifier base from configuration?
				// TODO maybe set issuer for cloud applications
				final AttributeFqn attributeId = AttributeFqns.newInstance(AbacUtils.mapCategory(meta.getCategory()),
						Optional.empty(), meta.getId());
				final AttributeBag<?> attributeValues = findAttributes(r.getValue());
				requestBuilder.putNamedAttributeIfAbsent(attributeId, attributeValues);
				if (log.isDebugEnabled())
					log.debug("RequestAttribute " + r + " added.");
			} else {
				if (log.isWarnEnabled())
					log.warn("RequestAttribute " + r + " will not be used in request because its value is null!");
			}
		}
		return requestBuilder.build(false);
	}

	@SuppressWarnings("unchecked")
	private AttributeBag<?> findAttributes(Object rawValue) {

		if (rawValue instanceof Collection) {
			return ATT_VALUE_FACTORIES.newAttributeBag((Collection<? extends Serializable>) rawValue);
		}
		if (rawValue instanceof Serializable) {
			return ATT_VALUE_FACTORIES.newAttributeBag(Collections.singleton((Serializable) rawValue));
		}

		log.warn(
				"Cannot decipher java object, defaulting to String datatype. If this is not correct, you must specify the datatype in the annotation.");
		//
		// Default to a string
		//
		return Bags.singletonAttributeBag(StandardDatatypes.STRING, StringValue.parse(rawValue.toString()));
	}

}
