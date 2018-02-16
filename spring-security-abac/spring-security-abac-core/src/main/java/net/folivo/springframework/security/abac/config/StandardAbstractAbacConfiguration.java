package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.AfterInvocationProviderManager;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.attributes.StandardRequestAttributeFactory;
import net.folivo.springframework.security.abac.contexthandler.RequestContextHandler;
import net.folivo.springframework.security.abac.method.AbacAnnotationPostRequestAttributeProvider;
import net.folivo.springframework.security.abac.method.AbacAnnotationPreRequestAttributeProvider;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.pep.AttributeBasedAccessDecisionVoter;
import net.folivo.springframework.security.abac.pep.AttributeBasedAfterInvocationProvider;
import net.folivo.springframework.security.abac.pep.PepEngine;
import net.folivo.springframework.security.abac.pep.PostProcessingPepEngine;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;

@Configuration
@EnableGlobalAuthentication
public abstract class StandardAbstractAbacConfiguration<T> {

	private RequestContextHandler<T> contextHandler;
	private AuthenticationConfiguration authConfig;

	@Bean
	protected PepEngine<T> pepEngine() {
		return new PostProcessingPepEngine<>(contextHandler, requestAttributePostProcessors());
	}

	@Bean
	protected AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
		decisionVoters.add(new AttributeBasedAccessDecisionVoter<>(pepEngine(), getContextClass(),
				AbacPreInvocationAttribute.class));
		AbstractAccessDecisionManager acdm = new AffirmativeBased(decisionVoters);
		// TODO bad workaound
		acdm.setAllowIfAllAbstainDecisions(true);
		return acdm;
	}

	@Bean
	protected AfterInvocationManager afterInvocationManager() {
		AfterInvocationProviderManager invocationProviderManager = new AfterInvocationProviderManager();
		List<AfterInvocationProvider> afterInvocationProviders = new ArrayList<>();
		afterInvocationProviders.add(new AttributeBasedAfterInvocationProvider<>(pepEngine(), getContextClass(),
				AbacPostInvocationAttribute.class));
		invocationProviderManager.setProviders(afterInvocationProviders);
		return invocationProviderManager;
	}

	@Bean
	protected List<RequestAttributeProvider<MethodInvocationContext>> requestAttributePreInvocationProvider() {
		List<RequestAttributeProvider<MethodInvocationContext>> providers = new ArrayList<>();
		providers.add(new AbacAnnotationPreRequestAttributeProvider(requestAttributeFactory()));
		providers.addAll(staticRequestAttributeProvider());
		AnnotationAwareOrderComparator.sort(providers);
		return providers;
	}

	@Bean
	protected List<RequestAttributeProvider<MethodInvocationContext>> requestAttributePostInvocationProvider() {
		List<RequestAttributeProvider<MethodInvocationContext>> providers = new ArrayList<>();
		providers.add(new AbacAnnotationPostRequestAttributeProvider(requestAttributeFactory()));
		providers.addAll(staticRequestAttributeProvider());
		AnnotationAwareOrderComparator.sort(providers);
		return providers;
	}

	@Bean
	protected Collection<RequestAttributeProvider<MethodInvocationContext>> staticRequestAttributeProvider() {
		return Collections.emptyList();
	}

	@Bean
	public RequestAttributeFactory requestAttributeFactory() {
		return new StandardRequestAttributeFactory();
	}

	@Autowired
	public void setContextHandler(RequestContextHandler<T> contextHandler) {
		this.contextHandler = contextHandler;
	}

	@Autowired
	public void setAuthConfig(AuthenticationConfiguration authConfig) {
		this.authConfig = authConfig;
	}

	// TODO catch?
	protected AuthenticationManager getAuthenticationManager() throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	abstract protected List<RequestAttributeProcessor<T>> requestAttributePostProcessors();

	abstract protected Class<T> getContextClass();

	protected AuthenticationConfiguration getAuthenticationConfig() {
		return authConfig;
	}

}
