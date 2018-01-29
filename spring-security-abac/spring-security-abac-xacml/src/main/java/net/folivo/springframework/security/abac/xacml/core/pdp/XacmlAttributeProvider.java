package net.folivo.springframework.security.abac.xacml.core.pdp;

/*-import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.AttributeProvider;
import org.ow2.authzforce.core.pdp.api.BaseDesignatedAttributeProvider;
import org.ow2.authzforce.core.pdp.api.CloseableDesignatedAttributeProvider;
import org.ow2.authzforce.core.pdp.api.EvaluationContext;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.api.value.BagDatatype;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;

public class XacmlAttributeProvider extends BaseDesignatedAttributeProvider {

	private final AttributeValueFactoryRegistry attributeValueFactoryRegistry;

	private XacmlAttributeProvider(String instanceID, AttributeValueFactoryRegistry attributeValueFactoryRegistry)
			throws IllegalArgumentException {
		super(instanceID);
		this.attributeValueFactoryRegistry = attributeValueFactoryRegistry;
	}

	public class XacmlDependencyAwareFactory implements DependencyAwareFactory {

		@Override
		public Set<AttributeDesignatorType> getDependencies() {
			return null;
		}

		@Override
		public CloseableDesignatedAttributeProvider getInstance(
				AttributeValueFactoryRegistry attributeValueFactoryRegistry, AttributeProvider depAttrProvider) {
			// TODO Auto-generated method stub
			return new XacmlAttributeProvider("test", attributeValueFactoryRegistry);
		}

	}

	@Override
	public Set<AttributeDesignatorType> getProvidedAttributes() {
		Set<AttributeDesignatorType> types = new HashSet<>();
		types.add(new AttributeDesignatorType(category, attributeId, dataType, issuer, mustBePresent));
		return types;
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

}*/
