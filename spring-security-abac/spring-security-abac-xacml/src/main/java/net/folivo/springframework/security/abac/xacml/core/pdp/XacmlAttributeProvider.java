package net.folivo.springframework.security.abac.xacml.core.pdp;

import java.io.IOException;
import java.util.Set;

import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.BaseDesignatedAttributeProvider;
import org.ow2.authzforce.core.pdp.api.EvaluationContext;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.BagDatatype;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;

public class XacmlAttributeProvider extends BaseDesignatedAttributeProvider {

	public XacmlAttributeProvider(String instanceID) throws IllegalArgumentException {
		super(instanceID);
	}

	@Override
	public Set<AttributeDesignatorType> getProvidedAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <AV extends AttributeValue> AttributeBag<AV> get(AttributeFqn attributeFQN, BagDatatype<AV> returnDatatype,
			EvaluationContext context) throws IndeterminateEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
