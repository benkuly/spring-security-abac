package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedPostInvocationAdvice;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.AfterInvocationProviderManager;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.access.intercept.aspectj.AspectJMethodSecurityInterceptor;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostInvocationAdviceProvider;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import net.folivo.springframework.security.abac.pep.RequestAttributePostProcessor;
import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationPostRequestAttributeProvider;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationPreRequestAttributeProvider;
import net.folivo.springframework.security.abac.prepost.AbacPreInvoacationAdvice;
import net.folivo.springframework.security.abac.prepost.PrePostInvocationAttributeFactory;
import net.folivo.springframework.security.abac.prepost.expression.ExpressionBasedInvocationAttributeFactory;
import net.folivo.springframework.security.abac.prepost.expression.ExpressionBasedRequestAttributePostProcessor;

//TODO better segregation between methodSecurity stuff and abac -> own Configuration
//more @Bean's?
public class AbacMethodSecurityConfiguration extends PepConfiguration<MethodInvocation> implements InitializingBean {

	public AbacMethodSecurityConfiguration(AuthenticationConfiguration authConfig, PdpConfiguration pdpConfig) {
		super(authConfig, pdpConfig);

	}

	protected AccessDecisionManager methodSecurityAccessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();

		// TODO maybe allow multiple voters
		// e.g. for local and remote pdp's at same time. if local pdp has no idea it can
		// ask remote pdp.
		decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(new AbacPreInvoacationAdvice(pepEngine())));

		return new AffirmativeBased(decisionVoters);
	}

	@Bean
	public MethodInterceptor methodSecurityInterceptor() throws Exception {
		// TODO
		MethodSecurityInterceptor methodSecurityInterceptor = isAspectJ() ? new AspectJMethodSecurityInterceptor()
				: new MethodSecurityInterceptor();
		methodSecurityInterceptor.setAccessDecisionManager(methodSecurityAccessDecisionManager());
		methodSecurityInterceptor.setAfterInvocationManager(methodSecurityAfterInvocationManager());
		methodSecurityInterceptor.setSecurityMetadataSource(methodSecurityMetadataSource());
		methodSecurityInterceptor.setAuthenticationManager(authenticationManager());
		RunAsManager runAsManager = runAsManager();
		if (runAsManager != null) {
			methodSecurityInterceptor.setRunAsManager(runAsManager);
		}
		return methodSecurityInterceptor;
	}

	@Bean
	public MethodSecurityMetadataSource methodSecurityMetadataSource() {
		PrePostInvocationAttributeFactory attributeFactory = new ExpressionBasedInvocationAttributeFactory();
		RequestAttributeProvider<MethodInvocation> preProvider = new AbacAnnotationPreRequestAttributeProvider(
				pdpConfig.requestAttributeFactory());
		RequestAttributeProvider<MethodInvocation> postProvider = new AbacAnnotationPostRequestAttributeProvider(
				pdpConfig.requestAttributeFactory());

		return new AbacAnnotationMethodSecurityMetadataSource(Arrays.asList(preProvider, postProvider),
				attributeFactory);
	}

	@Bean
	protected MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
		return new DefaultMethodSecurityExpressionHandler();
	}

	protected AfterInvocationManager methodSecurityAfterInvocationManager() {
		// TODO
		// TODO decide here the use of local or remote PDP
		AfterInvocationProviderManager invocationProviderManager = new AfterInvocationProviderManager();
		ExpressionBasedPostInvocationAdvice postAdvice = new ExpressionBasedPostInvocationAdvice(
				methodSecurityExpressionHandler());
		PostInvocationAdviceProvider postInvocationAdviceProvider = new PostInvocationAdviceProvider(postAdvice);
		List<AfterInvocationProvider> afterInvocationProviders = new ArrayList<>();
		afterInvocationProviders.add(postInvocationAdviceProvider);
		invocationProviderManager.setProviders(afterInvocationProviders);
		return invocationProviderManager;
	}

	@Override
	protected Collection<RequestAttributePostProcessor<MethodInvocation>> requestAttributePostProcessors() {
		Collection<RequestAttributePostProcessor<MethodInvocation>> requestAttributePostProcessors = new ArrayList<>();
		requestAttributePostProcessors.add(new ExpressionBasedRequestAttributePostProcessor(
				methodSecurityExpressionHandler(), pdpConfig.requestAttributeFactory()));
		return requestAttributePostProcessors;
	}

	private boolean isAspectJ() {
		// TODO
		return false;
	}

	protected RunAsManager runAsManager() {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

}
