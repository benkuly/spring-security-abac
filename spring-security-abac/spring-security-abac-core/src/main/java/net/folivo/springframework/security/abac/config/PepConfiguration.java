package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import net.folivo.springframework.security.abac.pep.RequestAttributePostProcessor;
import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.prepost.AbacAnnotationRequestAttributeProvider;
import net.folivo.springframework.security.abac.prepost.MethodInvocationContext;
import net.folivo.springframework.security.abac.prepost.PreInvocationAuthorizationVoter;
import net.folivo.springframework.security.abac.prepost.PrePostInvocationAttributeFactory;
import net.folivo.springframework.security.abac.prepost.expression.ExpressionBasedInvocationAttributeFactory;
import net.folivo.springframework.security.abac.prepost.expression.ExpressionBasedRequestAttributePostProcessor;

//TODO better segregation between methodSecurity stuff and abac -> own Configuration
//more @Bean's?
@Configuration
@EnableGlobalAuthentication
public class PepConfiguration implements InitializingBean {

	private final AuthenticationConfiguration authConfig;
	private final PdpConfiguration pdpConfig;

	private boolean disableAuthenticationRegistry;
	private AuthenticationManager authManager;
	private AuthenticationManagerBuilder authBuilder;
	private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
		@Override
		public <T> T postProcess(T object) {
			throw new IllegalStateException(ObjectPostProcessor.class.getName() + " is a required bean.");
		}
	};

	public PepConfiguration(AuthenticationConfiguration authConfig, PdpConfiguration pdpConfig) {
		this.authConfig = authConfig;
		this.pdpConfig = pdpConfig;

	}

	protected AccessDecisionManager methodSecurityAccessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();

		// TODO maybe allow multiple voters
		// e.g. for local and remote pdp's at same time. if local pdp has no idea it can
		// ask remote pdp.
		decisionVoters.add(new PreInvocationAuthorizationVoter(pdpConfig.getRequestFactory(), pdpConfig.getPdpClient(),
				pdpConfig.getResponseEvaluator(), requestAttributePostProcessors()));

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
		PrePostInvocationAttributeFactory<String> attributeFactory = new ExpressionBasedInvocationAttributeFactory(
				methodSecurityExpressionHandler());
		RequestAttributeProvider<MethodInvocationContext> annotationProvider = new AbacAnnotationRequestAttributeProvider(
				attributeFactory);

		return new AbacAnnotationMethodSecurityMetadataSource(Arrays.asList(annotationProvider));
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

	protected Collection<RequestAttributePostProcessor<MethodInvocationContext>> requestAttributePostProcessors() {
		Collection<RequestAttributePostProcessor<MethodInvocationContext>> requestAttributePostProcessors = new ArrayList<>();
		requestAttributePostProcessors
				.add(new ExpressionBasedRequestAttributePostProcessor(methodSecurityExpressionHandler()));
		return requestAttributePostProcessors;
	}

	/**
	 * Gets the {@link AuthenticationManager} to use. The default strategy is if
	 * {@link #configure(AuthenticationManagerBuilder)} method is overridden to use
	 * the {@link AuthenticationManagerBuilder} that was passed in. Otherwise,
	 * autowire the {@link AuthenticationManager} by type.
	 *
	 * @return
	 * @throws Exception
	 */
	protected AuthenticationManager authenticationManager() throws Exception {
		if (authManager != null) {

			// TODO research why we must create an eventPublisher
			DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
					.postProcess(new DefaultAuthenticationEventPublisher());
			authBuilder = new AuthenticationManagerBuilder(objectPostProcessor);
			authBuilder.authenticationEventPublisher(eventPublisher);

			configure(authBuilder);
			if (disableAuthenticationRegistry) {
				authManager = authConfig.getAuthenticationManager();
			} else {
				authManager = authBuilder.build();
			}
		}
		return authManager;
	}

	/**
	 * Sub classes can override this method to register different types of
	 * authentication. If not overridden,
	 * {@link #configure(AuthenticationManagerBuilder)} will attempt to autowire by
	 * type.
	 *
	 * @param auth
	 *            the {@link AuthenticationManagerBuilder} used to register
	 *            different authentication mechanisms for the global method
	 *            security.
	 * @throws Exception
	 */
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		this.disableAuthenticationRegistry = true;
	}

	@Autowired
	public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
		this.objectPostProcessor = objectPostProcessor;
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
