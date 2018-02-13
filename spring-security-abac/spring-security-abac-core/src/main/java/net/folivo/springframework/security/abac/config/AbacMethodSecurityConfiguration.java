package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.type.AnnotationMetadata;
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
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.util.Assert;

import net.folivo.springframework.security.abac.attributes.ProviderCollector;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.attributes.StandardProviderCollector;
import net.folivo.springframework.security.abac.method.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.method.AbacAnnotationPostRequestAttributeProvider;
import net.folivo.springframework.security.abac.method.AbacAnnotationPreRequestAttributeProvider;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityInterceptor;
import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityPointcut;
import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityPointcutAdvisor;
import net.folivo.springframework.security.abac.method.expression.ExpressionBasedRequestAttributePostProcessor;
import net.folivo.springframework.security.abac.pep.AttributeBasedAccessDecisionVoter;
import net.folivo.springframework.security.abac.pep.AttributeBasedAfterInvocationProvider;
import net.folivo.springframework.security.abac.pep.PepEngine;
import net.folivo.springframework.security.abac.pep.PostProcessingPepEngine;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationConfigAttributeFactory;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationConfigAttributeFactory;

@Configuration
@EnableGlobalAuthentication
public class AbacMethodSecurityConfiguration implements ImportBeanDefinitionRegistrar {

	private final Log log = LogFactory.getLog(AbacMethodSecurityConfiguration.class);
	protected final PdpConfiguration<?, ?, MethodInvocationContext> pdpConfig;
	protected final AuthenticationConfiguration authConfig;

	@Autowired
	public AbacMethodSecurityConfiguration(PdpConfiguration<?, ?, MethodInvocationContext> pdpConfig,
			AuthenticationConfiguration authConfig) {
		this.pdpConfig = pdpConfig;
		this.authConfig = authConfig;
	}

	// TODO aspectJ
	// TODO catch?
	@Bean
	protected MethodInterceptor methodSecurityInterceptor() throws Exception {
		AbacMethodSecurityInterceptor methodSecurityInterceptor = new AbacMethodSecurityInterceptor();
		methodSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
		methodSecurityInterceptor.setAfterInvocationManager(afterInvocationManager());
		methodSecurityInterceptor.setSecurityMetadataSource(methodSecurityMetadataSource());
		RunAsManager runAsManager = runAsManager();
		if (runAsManager != null) {
			methodSecurityInterceptor.setRunAsManager(runAsManager);
		}
		methodSecurityInterceptor.setAuthenticationManager(getAuthenticationManager());
		return methodSecurityInterceptor;
	}

	// TODO catch?
	protected AuthenticationManager getAuthenticationManager() throws Exception {
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
		ProviderCollector<MethodInvocationContext> preCollector = new StandardProviderCollector<>(
				requestAttributePreInvocationProvider());
		ProviderCollector<MethodInvocationContext> postCollector = new StandardProviderCollector<>(
				requestAttributePostInvocationProvider());
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
			Optional<PermissionEvaluator> permissionEvaluator, Optional<RoleHierarchy> roleHierarchy,
			Optional<GrantedAuthorityDefaults> grantedAuthorityDefaults,
			Optional<AuthenticationTrustResolver> trustResolver) {
		if (expressionHandler instanceof DefaultMethodSecurityExpressionHandler) {
			DefaultMethodSecurityExpressionHandler defaultExpressionHandler = (DefaultMethodSecurityExpressionHandler) expressionHandler;
			permissionEvaluator.ifPresent(defaultExpressionHandler::setPermissionEvaluator);
			roleHierarchy.ifPresent(defaultExpressionHandler::setRoleHierarchy);
			trustResolver.ifPresent(defaultExpressionHandler::setTrustResolver);
			grantedAuthorityDefaults.map(GrantedAuthorityDefaults::getRolePrefix)
					.ifPresent(defaultExpressionHandler::setDefaultRolePrefix);
		}
	}

	protected RunAsManager runAsManager() {
		return null;
	}

	// TODO make aspectJ-compatible
	// TODO catch?
	// TODO interface as return type?
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	protected AbstractPointcutAdvisor abacMethodSecurityPointcutAdvisor() throws Exception {
		// TODO is order of this(config) relevant? originally it is added
		return new AbacMethodSecurityPointcutAdvisor(abacMethodSecurityPointcut(), methodSecurityInterceptor());
	}

	// TODO interface as return type?
	@Bean
	protected StaticMethodMatcherPointcut abacMethodSecurityPointcut() {
		return new AbacMethodSecurityPointcut();
	}

	protected AdviceMode getAdviceMode() {
		return AdviceMode.PROXY;
	}

	protected boolean isProxyTargetClass() {
		return false;
	}

	// TODO no good way with this interface
	// TODO make aspectJ-compatible
	// TODO make it configurable
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		if (getAdviceMode() == AdviceMode.PROXY) {
			AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
			if (isProxyTargetClass()) {
				AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
			}
		}
	}
}
