package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.AfterInvocationProviderManager;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import net.folivo.springframework.security.abac.attributes.PreProcessingProviderCollector;
import net.folivo.springframework.security.abac.attributes.ProviderCollector;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.method.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.method.AbacAnnotationPostRequestAttributeProvider;
import net.folivo.springframework.security.abac.method.AbacAnnotationPreRequestAttributeProvider;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.method.expression.ExpressionBasedRequestAttributePostProcessor;
import net.folivo.springframework.security.abac.method.expression.ExpressionBasedRequestAttributePreProcessor;
import net.folivo.springframework.security.abac.pep.AttributeBasedAccessDecisionVoter;
import net.folivo.springframework.security.abac.pep.AttributeBasedAfterInvocationProvider;
import net.folivo.springframework.security.abac.pep.PepEngine;
import net.folivo.springframework.security.abac.pep.PostProcessingPepEngine;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationConfigAttributeFactory;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationConfigAttributeFactory;

//TODO AutoProxyRegistrar.class MethodSecurityMetadataSourceAdvisorRegistrar.class
@EnableGlobalAuthentication
@Configuration
public class AbacMethodSecurityConfiguration {

	protected final PdpConfiguration<?, ?, MethodInvocationContext> pdpConfig;
	protected final AuthenticationConfiguration authConfig;

	@Autowired
	public AbacMethodSecurityConfiguration(PdpConfiguration<?, ?, MethodInvocationContext> pdpConfig,
			AuthenticationConfiguration authConfig) {
		this.pdpConfig = pdpConfig;
		this.authConfig = authConfig;
	}

	// TODO aspectJ
	@Bean
	protected MethodInterceptor methodSecurityInterceptor() throws Exception {
		MethodSecurityInterceptor methodSecurityInterceptor = new MethodSecurityInterceptor();
		methodSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
		methodSecurityInterceptor.setAfterInvocationManager(afterInvocationManager());
		methodSecurityInterceptor.setSecurityMetadataSource(methodSecurityMetadataSource());
		RunAsManager runAsManager = runAsManager();
		if (runAsManager != null) {
			methodSecurityInterceptor.setRunAsManager(runAsManager);
		}
		methodSecurityInterceptor.setAuthenticationManager(authenticationManager());
		return methodSecurityInterceptor;
	}

	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	protected AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
		decisionVoters.add(new AttributeBasedAccessDecisionVoter<>(pepEngine(), MethodInvocationContext.class,
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
		afterInvocationProviders.add(new AttributeBasedAfterInvocationProvider<>(pepEngine(),
				MethodInvocationContext.class, AbacPostInvocationAttribute.class));
		invocationProviderManager.setProviders(afterInvocationProviders);
		return invocationProviderManager;
	}

	@Bean
	protected SecurityMetadataSource methodSecurityMetadataSource() {
		ProviderCollector<MethodInvocationContext> preCollector = new PreProcessingProviderCollector<>(
				requestAttributePreInvocationProvider(), requestAttributePreProcessors());
		ProviderCollector<MethodInvocationContext> postCollector = new PreProcessingProviderCollector<>(
				requestAttributePostInvocationProvider(), requestAttributePreProcessors());
		return new AbacAnnotationMethodSecurityMetadataSource(preCollector, postCollector,
				new AbacPreInvocationConfigAttributeFactory(), new AbacPostInvocationConfigAttributeFactory());
	}

	@Bean
	protected Collection<RequestAttributeProvider<MethodInvocationContext>> staticRequestAttributeProvider() {
		return Collections.emptyList();
	}

	@Bean
	protected List<RequestAttributeProvider<MethodInvocationContext>> requestAttributePreInvocationProvider() {
		List<RequestAttributeProvider<MethodInvocationContext>> providers = new ArrayList<>();
		providers.add(new AbacAnnotationPreRequestAttributeProvider(pdpConfig.requestAttributeFactory()));
		providers.addAll(staticRequestAttributeProvider());
		AnnotationAwareOrderComparator.sort(providers);
		return providers;
	}

	@Bean
	protected List<RequestAttributeProvider<MethodInvocationContext>> requestAttributePostInvocationProvider() {
		List<RequestAttributeProvider<MethodInvocationContext>> providers = new ArrayList<>();
		providers.add(new AbacAnnotationPostRequestAttributeProvider(pdpConfig.requestAttributeFactory()));
		providers.addAll(staticRequestAttributeProvider());
		AnnotationAwareOrderComparator.sort(providers);
		return providers;
	}

	@Bean
	protected List<RequestAttributeProcessor<MethodInvocationContext>> requestAttributePreProcessors() {
		List<RequestAttributeProcessor<MethodInvocationContext>> processors = new ArrayList<>();
		processors.add(new ExpressionBasedRequestAttributePreProcessor(expressionHandler()));
		AnnotationAwareOrderComparator.sort(processors);
		return processors;
	}

	@Bean
	protected List<RequestAttributeProcessor<MethodInvocationContext>> requestAttributePostProcessors() {
		List<RequestAttributeProcessor<MethodInvocationContext>> processors = new ArrayList<>();
		processors.add(new ExpressionBasedRequestAttributePostProcessor(expressionHandler()));
		AnnotationAwareOrderComparator.sort(processors);
		return processors;
	}

	@Bean
	protected PepEngine<MethodInvocationContext> pepEngine() {
		return new PostProcessingPepEngine<>(pdpConfig.requestContextHandler(), requestAttributePostProcessors());
	}

	@Bean
	protected MethodSecurityExpressionHandler expressionHandler() {
		return new DefaultMethodSecurityExpressionHandler();
	}

	@Autowired
	protected void configureExpressionHandler(MethodSecurityExpressionHandler expressionHandler,
			PermissionEvaluator permissionEvaluator, RoleHierarchy roleHierarchy, String defaultRolePrefix,
			AuthenticationTrustResolver trustResolver) {
		if (expressionHandler instanceof DefaultMethodSecurityExpressionHandler) {
			DefaultMethodSecurityExpressionHandler defaultExpressionHandler = (DefaultMethodSecurityExpressionHandler) expressionHandler;
			defaultExpressionHandler.setPermissionEvaluator(permissionEvaluator);
			defaultExpressionHandler.setRoleHierarchy(roleHierarchy);
			defaultExpressionHandler.setDefaultRolePrefix(defaultRolePrefix);
			defaultExpressionHandler.setTrustResolver(trustResolver);
		}
	}

	/**
	 * Provide a custom {@link RunAsManager} for the default implementation of
	 * {@link #methodSecurityInterceptor()}. The default is null.
	 *
	 * @return
	 */
	protected RunAsManager runAsManager() {
		return null;
	}

	// TODO make it configurable
	@Autowired
	protected void enableAutoProxy(BeanDefinitionRegistry registry) {
		if (getAdviceMode() == AdviceMode.PROXY) {
			AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
			if (isProxyTargetClass()) {
				AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
			}
			// TODO is order relevant? originally it is added
			BeanDefinitionBuilder advisor = BeanDefinitionBuilder
					.rootBeanDefinition(MethodSecurityMetadataSourceAdvisor.class);
			advisor.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			advisor.addConstructorArgValue("methodSecurityInterceptor");
			advisor.addConstructorArgReference("methodSecurityMetadataSource");
			advisor.addConstructorArgValue("methodSecurityMetadataSource");

			registry.registerBeanDefinition("metaDataSourceAdvisor", advisor.getBeanDefinition());
		}
		// TODO nicer way
		// else if (mode == AdviceMode.ASPECTJ) {
		// BeanDefinition interceptor =
		// registry.getBeanDefinition("methodSecurityInterceptor");
		//
		// BeanDefinitionBuilder aspect = BeanDefinitionBuilder.rootBeanDefinition(
		// "org.springframework.security.access.intercept.aspectj.aspect.AnnotationSecurityAspect");
		// aspect.setFactoryMethod("aspectOf");
		// aspect.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		// aspect.addPropertyValue("securityInterceptor", interceptor);
		//
		// registry.registerBeanDefinition("annotationSecurityAspect$0",
		// aspect.getBeanDefinition());
		// }
	}

	protected AdviceMode getAdviceMode() {
		return AdviceMode.PROXY;
	}

	protected boolean isProxyTargetClass() {
		return false;
	}
}
