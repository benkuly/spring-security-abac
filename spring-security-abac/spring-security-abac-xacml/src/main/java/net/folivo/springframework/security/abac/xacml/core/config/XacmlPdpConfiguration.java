package net.folivo.springframework.security.abac.xacml.core.config;

import java.io.IOException;
import java.util.Collections;

import org.ow2.authzforce.core.pdp.api.DecisionRequest;
import org.ow2.authzforce.core.pdp.api.DecisionResult;
import org.ow2.authzforce.core.pdp.api.EnvironmentProperties;
import org.ow2.authzforce.core.pdp.api.EnvironmentPropertyName;
import org.ow2.authzforce.core.pdp.api.PdpEngine;
import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.DefaultEnvironmentProperties;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.ow2.authzforce.core.xmlns.pdp.Pdp;
import org.ow2.authzforce.core.xmlns.pdp.StaticRootPolicyProvider;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;

import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.attributes.SimpleRequestAttributeFactory;
import net.folivo.springframework.security.abac.config.PdpConfiguration;
import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;
import net.folivo.springframework.security.abac.pep.PepEngine;
import net.folivo.springframework.security.abac.pep.SimplePepEngine;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlPdpClient;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlRequestFactory;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlResponseEvaluator;

public class XacmlPdpConfiguration implements PdpConfiguration<DecisionRequest, DecisionResult> {

	@Bean
	public PdpEngine pdpEngine() {
		Pdp pdpConf = new Pdp();
		pdpConf.setRootPolicyProvider(new StaticRootPolicyProvider("${PARENT_DIR}/policy.xml"));
		EnvironmentProperties envProps = new DefaultEnvironmentProperties(
				Collections.singletonMap(EnvironmentPropertyName.PARENT_DIR, "src/main/resources/xacml"));
		try {
			PdpEngineConfiguration pdpEngineConf = new PdpEngineConfiguration(pdpConf, envProps);
			return new BasePdpEngine(pdpEngineConf);
		} catch (IllegalArgumentException | IOException e) {
			throw new BeanCreationException("Couldn't create PdpEngine", e);
		}
	}

	@Override
	public RequestFactory<DecisionRequest> requestFactory() {
		return new XacmlRequestFactory(pdpEngine());
	}

	@Override
	public PdpClient<DecisionRequest, DecisionResult> pdpClient() {
		return new XacmlPdpClient(pdpEngine());
	}

	@Override
	public ResponseEvaluator<DecisionResult> responseEvaluator() {
		return new XacmlResponseEvaluator();
	}

	@Override
	public RequestAttributeFactory requestAttributeFactory() {
		return new SimpleRequestAttributeFactory();
	}

	@Override
	public PepEngine pepEngine() {
		return new SimplePepEngine<>(pdpClient(), responseEvaluator(), requestFactory());
	}
}
