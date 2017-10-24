package net.folivo.springframework.security.abac.xacml.core.config;

import java.util.Properties;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLProperties;

@Configuration
public class XacmlPdpConfiguration {

	@Bean
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
}
