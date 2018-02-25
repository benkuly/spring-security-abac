package net.folivo.springframework.security.abac.xacml.core.pdp;

import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.AttributeProvider;
import org.ow2.authzforce.core.pdp.api.EvaluationContext;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.BagDatatype;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

public class XacmlAttributeProvider implements AttributeProvider {

	private static final IndeterminateEvaluationException NO_VALUE_FROM_PIP = new IndeterminateEvaluationException(
			"No value found by PIP", XacmlStatusCode.PROCESSING_ERROR.value());

	@Override
	public <AV extends AttributeValue> AttributeBag<AV> get(AttributeFqn attributeFQN, BagDatatype<AV> returnDatatype,
			EvaluationContext context) throws IndeterminateEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

}
