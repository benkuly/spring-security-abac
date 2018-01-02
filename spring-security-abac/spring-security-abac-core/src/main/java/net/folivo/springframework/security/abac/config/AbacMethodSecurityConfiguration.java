package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.AfterInvocationProviderManager;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostInvocationAdviceProvider;
import org.springframework.security.access.prepost.PostInvocationAuthorizationAdvice;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.method.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.method.AbacAnnotationPostRequestAttributeProvider;
import net.folivo.springframework.security.abac.method.AbacAnnotationPreRequestAttributeProvider;
import net.folivo.springframework.security.abac.method.AbacPostInvoacationAdvice;
import net.folivo.springframework.security.abac.method.AbacPreInvoacationAdvice;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.method.expression.ExpressionBasedRequestAttributeAfterPostProcessor;
import net.folivo.springframework.security.abac.method.expression.ExpressionBasedRequestAttributeBeforePostProcessor;
import net.folivo.springframework.security.abac.method.expression.ExpressionBasedRequestAttributePreProcessor;
import net.folivo.springframework.security.abac.pep.PepClient;
import net.folivo.springframework.security.abac.pep.PostProcessingPepClient;
import net.folivo.springframework.security.abac.pep.PreProcessingProviderCollector;
import net.folivo.springframework.security.abac.pep.ProviderCollector;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationConfigAttributeFactory;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationConfigAttributeFactory;

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
		decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(new AbacPreInvoacationAdvice(prePepClient())));
		AbstractAccessDecisionManager acdm = new AffirmativeBased(decisionVoters);
		// TODO bad workaound
		acdm.setAllowIfAllAbstainDecisions(true);
		return acdm;
	}

	@Override
	protected AfterInvocationManager afterInvocationManager() {
		AfterInvocationProviderManager invocationProviderManager = new AfterInvocationProviderManager();
		// TODO maybe allow multiple voters
		// e.g. for local and remote pdp's at same time. if local pdp has no idea it can
		// ask remote pdp.
		PostInvocationAuthorizationAdvice postAdvice = new AbacPostInvoacationAdvice(postPepClient());
		PostInvocationAdviceProvider postInvocationAdviceProvider = new PostInvocationAdviceProvider(postAdvice);
		List<AfterInvocationProvider> afterInvocationProviders = new ArrayList<>();
		afterInvocationProviders.add(postInvocationAdviceProvider);
		invocationProviderManager.setProviders(afterInvocationProviders);
		return invocationProviderManager;
	}

	@Override
	public MethodSecurityMetadataSource methodSecurityMetadataSource() {
		Collection<ProviderCollector<MethodInvocation>> preCollectors = new ArrayList<>();
		Collection<ProviderCollector<MethodInvocation>> postCollectors = new ArrayList<>();
		ProviderCollector<MethodInvocation> preCollector = new PreProcessingProviderCollector<>(
				requestAttributePreInvocationProvider(), requestAttributePreProcessors(),
				new AbacPreInvocationConfigAttributeFactory());
		ProviderCollector<MethodInvocation> postCollector = new PreProcessingProviderCollector<>(
				requestAttributePostInvocationProvider(), requestAttributePreProcessors(),
				new AbacPostInvocationConfigAttributeFactory());
		preCollectors.add(preCollector);
		postCollectors.add(postCollector);
		return new AbacAnnotationMethodSecurityMetadataSource(preCollectors, postCollectors);
	}

	@Bean
	protected Collection<RequestAttributeProvider<MethodInvocation>> staticRequestAttributeProvider() {
		return Collections.emptyList();
	}

	@Bean
	protected List<RequestAttributeProvider<MethodInvocation>> requestAttributePreInvocationProvider() {
		List<RequestAttributeProvider<MethodInvocation>> providers = new ArrayList<>();
		providers.add(new AbacAnnotationPreRequestAttributeProvider(pdpConfig.requestAttributeFactory()));
		providers.addAll(staticRequestAttributeProvider());
		AnnotationAwareOrderComparator.sort(providers);
		return providers;
	}

	@Bean
	protected List<RequestAttributeProvider<MethodInvocation>> requestAttributePostInvocationProvider() {
		List<RequestAttributeProvider<MethodInvocation>> providers = new ArrayList<>();
		providers.add(new AbacAnnotationPostRequestAttributeProvider(pdpConfig.requestAttributeFactory()));
		providers.addAll(staticRequestAttributeProvider());
		AnnotationAwareOrderComparator.sort(providers);
		return providers;
	}

	@Bean
	protected List<RequestAttributeProcessor<MethodInvocation>> requestAttributePreProcessors() {
		List<RequestAttributeProcessor<MethodInvocation>> processors = new ArrayList<>();
		processors.add(new ExpressionBasedRequestAttributePreProcessor(getExpressionHandler()));
		AnnotationAwareOrderComparator.sort(processors);
		return processors;
	}

	@Bean
	protected List<RequestAttributeProcessor<MethodInvocation>> requestAttributeBeforePostProcessors() {
		List<RequestAttributeProcessor<MethodInvocation>> processors = new ArrayList<>();
		processors.add(new ExpressionBasedRequestAttributeBeforePostProcessor(getExpressionHandler()));
		AnnotationAwareOrderComparator.sort(processors);
		return processors;
	}

	@Bean
	protected List<RequestAttributeProcessor<MethodInvocationContext>> requestAttributeAfterPostProcessors() {
		List<RequestAttributeProcessor<MethodInvocationContext>> processors = new ArrayList<>();
		processors.add(new ExpressionBasedRequestAttributeAfterPostProcessor(getExpressionHandler()));
		AnnotationAwareOrderComparator.sort(processors);
		return processors;
	}

	@Bean
	public PepClient<MethodInvocation> prePepClient() {
		return new PostProcessingPepClient<>(pdpConfig.pepEngine(), requestAttributeBeforePostProcessors());
	}

	@Bean
	public PepClient<MethodInvocationContext> postPepClient() {
		return new PostProcessingPepClient<>(pdpConfig.pepEngine(), requestAttributeAfterPostProcessors());
	}

}
