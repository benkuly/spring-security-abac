package net.folivo.springframework.security.abac.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityInterceptor;

//TODO no good way with this interface
@Configuration
@EnableGlobalAuthentication
public class AbacAopConfiguration {

	AbacMethodSecurityInterceptor methodSecurityInterceptor;
	private AccessDecisionManager abacAccessDecisionManager;
	private AfterInvocationManager abacAfterInvocationManager;
	private SecurityMetadataSource abacMethodSecurityMetadataSource;
	private ApplicationContext context;

	// TODO aspectJ
	// TODO catch?
	@Bean
	protected MethodInterceptor abacMethodSecurityInterceptor() throws Exception {
		methodSecurityInterceptor = new AbacMethodSecurityInterceptor();
		methodSecurityInterceptor.setAccessDecisionManager(abacAccessDecisionManager);
		methodSecurityInterceptor.setAfterInvocationManager(abacAfterInvocationManager);
		methodSecurityInterceptor.setSecurityMetadataSource(abacMethodSecurityMetadataSource);
		RunAsManager runAsManager = runAsManager();
		if (runAsManager != null) {
			methodSecurityInterceptor.setRunAsManager(runAsManager);
		}
		methodSecurityInterceptor.setAuthenticationManager(getAuthenticationManager());
		return methodSecurityInterceptor;
	}

	// TODO
	protected RunAsManager runAsManager() {
		return null;
	}

	// TODO
	protected boolean isProxyTargetClass() {
		return false;
	}

	@Autowired
	public void setAbacAccessDecisionManager(AccessDecisionManager abacAccessDecisionManager) {
		this.abacAccessDecisionManager = abacAccessDecisionManager;
	}

	@Autowired
	public void setAbacAfterInvocationManager(AfterInvocationManager abacAfterInvocationManager) {
		this.abacAfterInvocationManager = abacAfterInvocationManager;
	}

	@Autowired
	public void setAbacMethodSecurityMetadataSource(SecurityMetadataSource abacMethodSecurityMetadataSource) {
		this.abacMethodSecurityMetadataSource = abacMethodSecurityMetadataSource;
	}

	@Autowired
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	private AuthenticationConfiguration authConfig;

	@Autowired
	protected void setAuthConfig(AuthenticationConfiguration authConfig) {
		this.authConfig = authConfig;
	}

	private AuthenticationManager getAuthenticationManager() throws Exception {
		return authConfig.getAuthenticationManager();
	}

}
