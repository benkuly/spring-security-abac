package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.access.vote.AffirmativeBased;

import net.folivo.springframework.security.abac.prepost.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.prepost.HierarchicalMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.prepost.PdpUsingPreInvocationAuthorizationAdvice;
import net.folivo.springframework.security.abac.prepost.PreInvocationAuthorizationAdviceVoter;
import net.folivo.springframework.security.abac.prepost.PrePostRequestAttributeFactory;
import net.folivo.springframework.security.abac.prepost.expression.ExpressionBasedInvocationAttributeFactory;

@Configuration
public class AbacPepConfiguration {

	private MethodSecurityInterceptor methodSecurityInterceptor;

	private MethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();

	protected AccessDecisionManager accessDecisionManager() {
		// TODO
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
		PdpUsingPreInvocationAuthorizationAdvice expressionAdvice = new PdpUsingPreInvocationAuthorizationAdvice();

		// TODO decide here the use of local or remote PDP

		decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(expressionAdvice));

		return new AffirmativeBased(decisionVoters);
	}

	@Bean
	public MethodInterceptor methodSecurityInterceptor() throws Exception {
		// TODO
		this.methodSecurityInterceptor = isAspectJ() ? new AspectJMethodSecurityInterceptor()
				: new MethodSecurityInterceptor();
		methodSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
		methodSecurityInterceptor.setAfterInvocationManager(afterInvocationManager());
		methodSecurityInterceptor.setSecurityMetadataSource(methodSecurityMetadataSource());
		RunAsManager runAsManager = runAsManager();
		if (runAsManager != null) {
			methodSecurityInterceptor.setRunAsManager(runAsManager);
		}
		return this.methodSecurityInterceptor;
	}

	@Bean
	public MethodSecurityMetadataSource methodSecurityMetadataSource() {
		List<MethodSecurityMetadataSource> sources = new ArrayList<>();
		PrePostRequestAttributeFactory<String> attributeFactory = new ExpressionBasedInvocationAttributeFactory(
				getExpressionHandler());
		// TODO other MetadataSources
		sources.add(new AbacAnnotationMethodSecurityMetadataSource(attributeFactory));
		return new HierarchicalMethodSecurityMetadataSource(sources);
	}

	protected MethodSecurityExpressionHandler getExpressionHandler() {
		return expressionHandler;
	}

	protected AfterInvocationManager afterInvocationManager() {
		// TODO
		// TODO decide here the use of local or remote PDP
		AfterInvocationProviderManager invocationProviderManager = new AfterInvocationProviderManager();
		ExpressionBasedPostInvocationAdvice postAdvice = new ExpressionBasedPostInvocationAdvice(
				getExpressionHandler());
		PostInvocationAdviceProvider postInvocationAdviceProvider = new PostInvocationAdviceProvider(postAdvice);
		List<AfterInvocationProvider> afterInvocationProviders = new ArrayList<>();
		afterInvocationProviders.add(postInvocationAdviceProvider);
		invocationProviderManager.setProviders(afterInvocationProviders);
		return invocationProviderManager;
	}

	private boolean isAspectJ() {
		// TODO
		return false;
	}

	protected RunAsManager runAsManager() {
		return null;
	}

}
