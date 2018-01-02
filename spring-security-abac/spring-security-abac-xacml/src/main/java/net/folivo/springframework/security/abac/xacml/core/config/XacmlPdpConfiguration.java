package net.folivo.springframework.security.abac.xacml.core.config;

import java.util.Properties;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLProperties;

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

public class XacmlPdpConfiguration implements PdpConfiguration<Request, Response> {

	@Bean
	public PDPEngine getPdpEngine() {
		Properties props = new Properties();
		props.setProperty(XACMLProperties.PROP_ROOTPOLICIES, "policy");
		props.setProperty("policy.file", "src/main/resources/xacml/policy.xml");
		try {
			return PDPEngineFactory.newInstance().newEngine(props);
		} catch (FactoryException e) {
			throw new BeanCreationException("PDPEngine", "Failed to create a PDPEngine", e);
		}
	}

	@Override
	public RequestFactory<Request> requestFactory() {
		return new XacmlRequestFactory();
	}

	@Override
	public PdpClient<Request, Response> pdpClient() {
		return new XacmlPdpClient(getPdpEngine());
	}

	@Override
	public ResponseEvaluator<Response> responseEvaluator() {
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
