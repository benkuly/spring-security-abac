package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.config.AbacMethodSecurityConfiguration;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.xacml.core.config.XacmlPdpConfiguration;

@Profile("abacSecurity")
@Import({ XacmlPdpConfiguration.class })
@Configuration
public class AbacSecurityConfig extends AbacMethodSecurityConfiguration {

	@Autowired
	public AbacSecurityConfig(XacmlPdpConfiguration pdpConfig, AuthenticationConfiguration authConfig) {
		super(pdpConfig, authConfig);
	}

	@Override
	protected Collection<RequestAttributeProvider<MethodInvocationContext>> staticRequestAttributeProvider() {
		return Collections.singleton(subjProvider());
	}

	@Bean
	public SubjectAttributeProvider subjProvider() {
		return new SubjectAttributeProvider(pdpConfig.requestAttributeFactory());
	}
}
