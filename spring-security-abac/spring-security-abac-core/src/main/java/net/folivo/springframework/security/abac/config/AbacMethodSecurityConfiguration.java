package net.folivo.springframework.security.abac.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import net.folivo.springframework.security.abac.method.AbacAnnotationMethodSecurityMetadataSource;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.method.expression.ExpressionBasedRequestAttributePostProcessor;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationConfigAttributeFactory;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationConfigAttributeFactory;

public class AbacMethodSecurityConfiguration extends StandardAbstractAbacConfiguration<MethodInvocationContext> {

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
		processors.add(new ExpressionBasedRequestAttributePostProcessor(abacExpressionHandler()));
		AnnotationAwareOrderComparator.sort(processors);
		return processors;
	}

	@Bean
	protected MethodSecurityExpressionHandler abacExpressionHandler() {
		return new DefaultMethodSecurityExpressionHandler();
	}

	@Autowired
	protected void configureExpressionHandler(MethodSecurityExpressionHandler abacExpressionHandler,
			Optional<PermissionEvaluator> permissionEvaluator, Optional<RoleHierarchy> roleHierarchy,
			Optional<GrantedAuthorityDefaults> grantedAuthorityDefaults,
			Optional<AuthenticationTrustResolver> trustResolver) {
		if (abacExpressionHandler instanceof DefaultMethodSecurityExpressionHandler) {
			DefaultMethodSecurityExpressionHandler defaultExpressionHandler = (DefaultMethodSecurityExpressionHandler) abacExpressionHandler;
			permissionEvaluator.ifPresent(defaultExpressionHandler::setPermissionEvaluator);
			roleHierarchy.ifPresent(defaultExpressionHandler::setRoleHierarchy);
			trustResolver.ifPresent(defaultExpressionHandler::setTrustResolver);
			grantedAuthorityDefaults.map(GrantedAuthorityDefaults::getRolePrefix)
					.ifPresent(defaultExpressionHandler::setDefaultRolePrefix);
		}
	}

	@Override
	protected Class<MethodInvocationContext> getContextClass() {
		return MethodInvocationContext.class;
	}
}
