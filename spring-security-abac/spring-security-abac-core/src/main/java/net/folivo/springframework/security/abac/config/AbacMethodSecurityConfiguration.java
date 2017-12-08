package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.AfterInvocationProviderManager;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostInvocationAdviceProvider;
import org.springframework.security.access.prepost.PostInvocationAuthorizationAdvice;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import net.folivo.springframework.security.abac.pep.PepClient;
import net.folivo.springframework.security.abac.pep.RequestAttributePostProcessor;
import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;
import net.folivo.springframework.security.abac.pep.SimplePepClient;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationPostRequestAttributeProvider;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationPreRequestAttributeProvider;
import net.folivo.springframework.security.abac.prepost.AbacPostInvoacationAdvice;
import net.folivo.springframework.security.abac.prepost.AbacPreInvoacationAdvice;
import net.folivo.springframework.security.abac.prepost.PrePostInvocationAttributeFactory;
import net.folivo.springframework.security.abac.prepost.expression.ExpressionBasedInvocationAttributeFactory;
import net.folivo.springframework.security.abac.prepost.expression.ExpressionBasedRequestAttributePostProcessor;

//this is only a workaround solution because standalone method security is so weired
@Configuration
public class AbacMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

	protected PdpConfiguration<?, ?> pdpConfig;

	@Autowired
	public void setPdpConfig(PdpConfiguration<?, ?> pdpConfig) {
		this.pdpConfig = pdpConfig;
	}

	// TODO copy paste
	@Override
	protected AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
		// TODO maybe allow multiple voters
		// e.g. for local and remote pdp's at same time. if local pdp has no idea it can
		// ask remote pdp.
		decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(new AbacPreInvoacationAdvice(pepClient())));
		return new AffirmativeBased(decisionVoters);
	}

	@Override
	protected AfterInvocationManager afterInvocationManager() {
		AfterInvocationProviderManager invocationProviderManager = new AfterInvocationProviderManager();
		// TODO maybe allow multiple voters
		// e.g. for local and remote pdp's at same time. if local pdp has no idea it can
		// ask remote pdp.
		PostInvocationAuthorizationAdvice postAdvice = new AbacPostInvoacationAdvice(pepClient());
		PostInvocationAdviceProvider postInvocationAdviceProvider = new PostInvocationAdviceProvider(postAdvice);
		List<AfterInvocationProvider> afterInvocationProviders = new ArrayList<>();
		afterInvocationProviders.add(postInvocationAdviceProvider);
		invocationProviderManager.setProviders(afterInvocationProviders);
		return invocationProviderManager;
	}

	@Override
	public MethodSecurityMetadataSource methodSecurityMetadataSource() {
		RequestAttributeProvider<MethodInvocation> preProvider = new AbacAnnotationPreRequestAttributeProvider(
				pdpConfig.requestAttributeFactory());
		RequestAttributeProvider<MethodInvocation> postProvider = new AbacAnnotationPostRequestAttributeProvider(
				pdpConfig.requestAttributeFactory());
		// TODO more provider

		PrePostInvocationAttributeFactory attributeFactory = new ExpressionBasedInvocationAttributeFactory();
		return new AbacAnnotationMethodSecurityMetadataSource(Arrays.asList(preProvider, postProvider),
				attributeFactory);
	}

	// @Bean
	protected Collection<RequestAttributePostProcessor<MethodInvocation>> requestAttributePostProcessors() {
		Collection<RequestAttributePostProcessor<MethodInvocation>> requestAttributePostProcessors = new ArrayList<>();
		requestAttributePostProcessors.add(new ExpressionBasedRequestAttributePostProcessor(getExpressionHandler(),
				pdpConfig.requestAttributeFactory()));
		return requestAttributePostProcessors;
	}

	// @Bean
	public PepClient<MethodInvocation> pepClient() {
		return new SimplePepClient<>(pdpConfig.pepEngine(), requestAttributePostProcessors());
	}

}
