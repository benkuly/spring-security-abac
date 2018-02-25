package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

import net.folivo.springframework.security.abac.attributes.ProviderCollector;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.attributes.StandardProviderCollector;
import net.folivo.springframework.security.abac.expression.ExpressionBasedRequestAttributeProcessor;
import net.folivo.springframework.security.abac.method.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationConfigAttributeFactory;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationConfigAttributeFactory;

public class AbacMethodSecurityConfiguration extends StandardAbstractAbacConfiguration<MethodInvocationContext>
		implements SmartInitializingSingleton {

	private MethodSecurityExpressionHandler expressionHandler;
	private DefaultMethodSecurityExpressionHandler defaultExpressionHandler;
	private ApplicationContext context;

	@Bean
	protected SecurityMetadataSource abacMethodSecurityMetadataSource() {
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
		processors.add(new ExpressionBasedRequestAttributeProcessor(abacExpressionHandler()));
		AnnotationAwareOrderComparator.sort(processors);
		return processors;
	}

	protected MethodSecurityExpressionHandler createAbacExpressionHandler() {
		defaultExpressionHandler = new DefaultMethodSecurityExpressionHandler();
		return defaultExpressionHandler;
	}

	@Bean
	protected MethodSecurityExpressionHandler abacExpressionHandler() {
		if (expressionHandler == null)
			expressionHandler = createAbacExpressionHandler();
		return expressionHandler;
	}

	protected void initializeExpressionHandler() {
		if (defaultExpressionHandler != null) {
			getSingleBean(PermissionEvaluator.class).ifPresent(defaultExpressionHandler::setPermissionEvaluator);
			getSingleBean(RoleHierarchy.class).ifPresent(defaultExpressionHandler::setRoleHierarchy);
			getSingleBean(AuthenticationTrustResolver.class).ifPresent(defaultExpressionHandler::setTrustResolver);
			getSingleBean(GrantedAuthorityDefaults.class).map(GrantedAuthorityDefaults::getRolePrefix)
					.ifPresent(defaultExpressionHandler::setDefaultRolePrefix);
		}
	}

	private <T> Optional<T> getSingleBean(Class<T> type) {
		String[] beanNamesForType = this.context.getBeanNamesForType(type);
		if (beanNamesForType == null || beanNamesForType.length != 1) {
			return Optional.empty();
		}
		return Optional.of(context.getBean(beanNamesForType[0], type));
	}

	@Override
	protected Class<MethodInvocationContext> getSecurityContextClass() {
		return MethodInvocationContext.class;
	}

	@Override
	public void afterSingletonsInstantiated() {
		initializeExpressionHandler();
	}

	@Autowired
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}
}
