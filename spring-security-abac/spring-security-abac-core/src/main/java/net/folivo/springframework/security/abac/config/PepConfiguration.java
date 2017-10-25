package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
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
import org.springframework.security.access.vote.AffirmativeBased;

import net.folivo.springframework.security.abac.expression.ExpressionBasedInvocationAttributeFactory;
import net.folivo.springframework.security.abac.expression.ExpressionBasedRequestAttributeConverter;
import net.folivo.springframework.security.abac.pdp.RequestAttributeConverter;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.prepost.HierarchicalMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.prepost.PreInvocationAuthorizationVoter;
import net.folivo.springframework.security.abac.prepost.PrePostInvocationAttributeFactory;

public class PepConfiguration {

	private final PdpConfiguration pdpConfig;

	private MethodSecurityInterceptor methodSecurityInterceptor;

	private MethodSecurityExpressionHandler expressionHandler;
	private Collection<RequestAttributeConverter> requestAttributeConverters;

	public PepConfiguration(PdpConfiguration pdpConfig) {
		this.pdpConfig = pdpConfig;

		expressionHandler = new DefaultMethodSecurityExpressionHandler();
		requestAttributeConverters = new ArrayList<>();
		requestAttributeConverters.add(new ExpressionBasedRequestAttributeConverter(expressionHandler));
	}

	protected AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();

		// TODO maybe allow multiple voters
		// e.g. for local and remote pdp's at same time. if local pdp has no idea it can
		// ask remote pdp.
		decisionVoters.add(new PreInvocationAuthorizationVoter(getRequestAttributeConverters(),
				pdpConfig.getRequestFactory(), pdpConfig.getPdpClient(), pdpConfig.getResponseEvaluator()));

		return new AffirmativeBased(decisionVoters);
	}

	@Bean
	public MethodInterceptor methodSecurityInterceptor() throws Exception {
		// TODO
		methodSecurityInterceptor = isAspectJ() ? new AspectJMethodSecurityInterceptor()
				: new MethodSecurityInterceptor();
		methodSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
		methodSecurityInterceptor.setAfterInvocationManager(afterInvocationManager());
		methodSecurityInterceptor.setSecurityMetadataSource(methodSecurityMetadataSource());
		RunAsManager runAsManager = runAsManager();
		if (runAsManager != null) {
			methodSecurityInterceptor.setRunAsManager(runAsManager);
		}
		return methodSecurityInterceptor;
	}

	@Bean
	public MethodSecurityMetadataSource methodSecurityMetadataSource() {
		List<MethodSecurityMetadataSource> sources = new ArrayList<>();
		PrePostInvocationAttributeFactory<String> attributeFactory = new ExpressionBasedInvocationAttributeFactory(
				getExpressionHandler());
		// TODO more MetadataSources!
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

	protected Collection<RequestAttributeConverter> getRequestAttributeConverters() {
		return requestAttributeConverters;
	}

	private boolean isAspectJ() {
		// TODO
		return false;
	}

	protected RunAsManager runAsManager() {
		return null;
	}

}
