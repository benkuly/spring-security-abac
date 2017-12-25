package net.folivo.springframework.security.abac.method.expression;

import java.util.Collection;
import java.util.Collections;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pep.RequestAttributePostProcessor;

//TODO implements aop needed?
public class ExpressionBasedRequestAttributeBeforePostProcessor
		implements RequestAttributePostProcessor<MethodInvocation>, AopInfrastructureBean {

	private final MethodSecurityExpressionHandler expressionHandler;

	public ExpressionBasedRequestAttributeBeforePostProcessor(MethodSecurityExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	@Override
	public boolean supportsValue(Class<?> clazz) {
		return Expression.class.isAssignableFrom(clazz);
	}

	@Override
	public Collection<RequestAttribute> process(RequestAttribute attr, MethodInvocation context) {
		Expression expr = (Expression) attr.getValue();
		EvaluationContext evaluationContext = expressionHandler
				.createEvaluationContext(SecurityContextHolder.getContext().getAuthentication(), context);
		attr.setValue(expr.getValue(evaluationContext));
		return Collections.singleton(attr);
	}
}
