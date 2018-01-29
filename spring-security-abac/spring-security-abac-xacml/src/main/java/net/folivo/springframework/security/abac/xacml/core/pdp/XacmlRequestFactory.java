package net.folivo.springframework.security.abac.xacml.core.pdp;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.AttributeFqns;
import org.ow2.authzforce.core.pdp.api.DecisionRequest;
import org.ow2.authzforce.core.pdp.api.DecisionRequestBuilder;
import org.ow2.authzforce.core.pdp.api.PdpEngine;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.core.pdp.api.value.StringValue;
import org.ow2.authzforce.core.pdp.impl.value.StandardAttributeValueFactories;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;
import net.folivo.springframework.security.abac.contexthandler.RequestFactory;

public class XacmlRequestFactory implements RequestFactory<DecisionRequest> {

	private static final Log log = LogFactory.getLog(XacmlRequestFactory.class);
	private final PdpEngine pdp;
	private static final AttributeValueFactoryRegistry ATT_VALUE_FACTORIES = StandardAttributeValueFactories
			.getRegistry(false, Optional.empty());

	public XacmlRequestFactory(PdpEngine pdp) {
		this.pdp = pdp;
	}

	// TODO throw exception when build didn't work
	@Override
	public DecisionRequest build(Collection<RequestAttribute> requestAttrs) {

		final DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(-1, requestAttrs.size());

		for (RequestAttribute r : requestAttrs) {

			final RequestAttributeMetadata meta = r.getMetadata();
			if (r.getValue() != null) {
				// TODO maybe use identifier base from configuration?
				// TODO maybe set issuer for cloud applications
				final AttributeFqn attributeId = AttributeFqns.newInstance(findCategory(meta.getCategory()),
						Optional.empty(), meta.getId());
				final AttributeBag<?> attributeValues = findAttributes(r.getValue());
				requestBuilder.putNamedAttributeIfAbsent(attributeId, attributeValues);
			}
			// TODO that should really never happen
			if (log.isDebugEnabled())
				log.debug("RequestAttribute with id '" + meta.getId()
						+ "' will not be used in request because its value is null!");
		}
		return requestBuilder.build(false);
	}

	@SuppressWarnings("unchecked")
	private AttributeBag<?> findAttributes(Object rawValue) {

		if (rawValue instanceof Serializable) {
			return ATT_VALUE_FACTORIES.newAttributeBag(Collections.singleton((Serializable) rawValue));
		}
		if (rawValue instanceof Collection) {
			return ATT_VALUE_FACTORIES.newAttributeBag((Collection<? extends Serializable>) rawValue);
		}

		log.warn(
				"Cannot decipher java object, defaulting to String datatype. If this is not correct, you must specify the datatype in the annotation.");
		//
		// Default to a string
		//
		return Bags.singletonAttributeBag(StandardDatatypes.STRING, StringValue.parse(rawValue.toString()));
	}

	private String findCategory(AttributeCategory cat) {
		switch (cat) {
		case SUBJECT:
			return XacmlAttributeCategory.XACML_1_0_ACCESS_SUBJECT.value();
		case RESOURCE:
			return XacmlAttributeCategory.XACML_3_0_RESOURCE.value();
		case ACTION:
			return XacmlAttributeCategory.XACML_3_0_ACTION.value();
		case ENVIRONMENT:
			// TODO maybe throw exception for default
		default:
			return XacmlAttributeCategory.XACML_3_0_ENVIRONMENT.value();
		}
	}

}
