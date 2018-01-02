package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.Collection;
import java.util.Collections;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.config.AbacMethodSecurityConfiguration;
import net.folivo.springframework.security.abac.xacml.core.config.XacmlPdpConfiguration;

@Profile("abacSecurity")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({ XacmlPdpConfiguration.class })
public class AbacSecurityConfig extends AbacMethodSecurityConfiguration {

	@Override
	protected Collection<RequestAttributeProvider<MethodInvocation>> staticRequestAttributeProvider() {
		return Collections.singleton(subjProvider());
	}

	@Bean
	public SubjectAttributeProvider subjProvider() {
		return new SubjectAttributeProvider(pdpConfig.requestAttributeFactory());
	}
}
