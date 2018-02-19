package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.config.AbacAopAdvisorConfiguration;
import net.folivo.springframework.security.abac.config.AbacAopConfiguration;
import net.folivo.springframework.security.abac.config.AbacMethodSecurityConfiguration;
import net.folivo.springframework.security.abac.config.AutoProxyRegistration;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.xacml.core.config.XacmlPdpMethodInvocationContextConfiguration;

@Profile("abacSecurity")
@Import({ XacmlPdpMethodInvocationContextConfiguration.class, AbacAopAdvisorConfiguration.class,
		AbacAopConfiguration.class, AutoProxyRegistration.class })
@Configuration
public class AbacSecurityConfig extends AbacMethodSecurityConfiguration {

	@Override
	protected Collection<RequestAttributeProvider<MethodInvocationContext>> staticRequestAttributeProvider() {
		return Collections.singleton(subjProvider());
	}

	@Bean
	public SubjectAttributeProvider subjProvider() {
		return new SubjectAttributeProvider(requestAttributeFactory());
	}
}
