package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.util.Assert;

import net.folivo.springframework.security.abac.attributes.ProviderCollector;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.attributes.StandardProviderCollector;
import net.folivo.springframework.security.abac.method.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityInterceptor;
import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityPointcut;
import net.folivo.springframework.security.abac.method.aopalliance.AbacMethodSecurityPointcutAdvisor;
import net.folivo.springframework.security.abac.method.expression.ExpressionBasedRequestAttributePostProcessor;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationConfigAttributeFactory;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationConfigAttributeFactory;

public class AbacMethodSecurityConfiguration extends StandardAbstractAbacConfiguration<MethodInvocationContext>
		implements ImportBeanDefinitionRegistrar {

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

	@Bean
	protected SecurityMetadataSource methodSecurityMetadataSource() {
		ProviderCollector<MethodInvocationContext> preCollector = new StandardProviderCollector<>(
				requestAttributePreInvocationProvider());
		ProviderCollector<MethodInvocationContext> postCollector = new StandardProviderCollector<>(
				requestAttributePostInvocationProvider());
		return new AbacAnnotationMethodSecurityMetadataSource(preCollector, postCollector,
				new AbacPreInvocationConfigAttributeFactory(), new AbacPostInvocationConfigAttributeFactory());
	}

	@Override
	protected List<RequestAttributeProcessor<MethodInvocationContext>> requestAttributePostProcessors() {
		List<RequestAttributeProcessor<MethodInvocationContext>> processors = new ArrayList<>();
		processors.add(new ExpressionBasedRequestAttributePostProcessor(expressionHandler()));
		AnnotationAwareOrderComparator.sort(processors);
		return processors;
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

	@Override
	protected Class<MethodInvocationContext> getContextClass() {
		return MethodInvocationContext.class;
	}
}
