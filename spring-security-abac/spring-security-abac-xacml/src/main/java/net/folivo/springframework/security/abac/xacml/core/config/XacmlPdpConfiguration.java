package net.folivo.springframework.security.abac.xacml.core.config;

import java.util.Properties;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Configuration;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLProperties;

import net.folivo.springframework.security.abac.config.PdpConfiguration;
import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;
import net.folivo.springframework.security.abac.pep.PepEngine;
import net.folivo.springframework.security.abac.pep.SimplePepEngine;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlPdpClient;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlRequestAttributeFactory;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlRequestFactory;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlResponseEvaluator;

@Configuration
public class XacmlPdpConfiguration implements PdpConfiguration<Request, Response> {

	public PDPEngine getPdpEngine() {
		Properties props = new Properties();
		props.setProperty(XACMLProperties.PROP_ROOTPOLICIES, "policy");
		props.setProperty("policy.file", "src/main/resources/XACML/policy.xml");
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
		return new XacmlRequestAttributeFactory();
	}

	@Override
	public PepEngine pepEngine() {
		return new SimplePepEngine<Request, Response>(pdpClient(), responseEvaluator(), requestFactory());
	}
}
