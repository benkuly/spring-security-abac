package net.folivo.springframework.security.abac.xacml.core.pip;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.AttributeFqns;
import org.ow2.authzforce.core.pdp.api.AttributeProvider;
import org.ow2.authzforce.core.pdp.api.EvaluationContext;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.BagDatatype;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import net.folivo.springframework.security.abac.attributes.RequestAttributeMetadata;
import net.folivo.springframework.security.abac.attributes.StandardRequestAttributeMetadata;
import net.folivo.springframework.security.abac.context.RequestContextMapper;
import net.folivo.springframework.security.abac.pip.ContextMappingPipEngine;
import net.folivo.springframework.security.abac.xacml.core.config.AbacUtils;

public class XacmlAttributeProvider<T> implements AttributeProvider {

	private static final IndeterminateEvaluationException NO_VALUE_FROM_PIP = new IndeterminateEvaluationException(
			"No value found by PIP", XacmlStatusCode.PROCESSING_ERROR.value());

	private final ContextMappingPipEngine<T> pip;

	public XacmlAttributeProvider(ContextMappingPipEngine<T> pip) {
		this.pip = pip;
	}

	@Override
	public <AV extends AttributeValue> AttributeBag<AV> get(AttributeFqn attributeFQN, BagDatatype<AV> returnDatatype,
			EvaluationContext context) throws IndeterminateEvaluationException {
		// TODO performance... slow?
		String requestIdentifier = context
				.getNamedAttributeValue(
						AttributeFqns.newInstance(AbacUtils.mapCategory(RequestContextMapper.ATTR_CAT),
								Optional.empty(), RequestContextMapper.ATTR_ID),
						StandardDatatypes.STRING.getBagDatatype())
				.getSingleElement().getUnderlyingValue();
		Collection<RequestAttributeMetadata> metadata = Collections.singleton(new StandardRequestAttributeMetadata(
				AbacUtils.mapCategory(attributeFQN.getCategory()), attributeFQN.getId()));
		pip.attributeQuery(requestIdentifier, metadata);
		return Bags.emptyAttributeBag(returnDatatype.getElementType(), NO_VALUE_FROM_PIP);
	}

}
