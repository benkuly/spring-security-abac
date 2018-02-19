package net.folivo.springframework.security.abac.config;

import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityPointcut;
import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityPointcutAdvisor;

@Configuration
public class AbacAopAdvisorConfiguration {
	// TODO catch?
	// TODO interface as return type?
	// TODO how to ensure, that this is right inteceptor?
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	protected AbstractPointcutAdvisor abacMethodSecurityPointcutAdvisor() throws Exception {
		// TODO is order of this(config) relevant? originally it is added
		return new AbacMethodSecurityPointcutAdvisor(abacMethodSecurityPointcut(), "abacMethodSecurityInterceptor");
	}

	// TODO interface as return type?
	// @Bean
	// @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	protected StaticMethodMatcherPointcut abacMethodSecurityPointcut() {
		return new AbacMethodSecurityPointcut();
	}
}
