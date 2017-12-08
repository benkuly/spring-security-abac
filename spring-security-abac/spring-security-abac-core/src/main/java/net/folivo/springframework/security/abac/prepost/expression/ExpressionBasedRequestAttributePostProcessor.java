package net.folivo.springframework.security.abac.prepost.expression;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;
import net.folivo.springframework.security.abac.pep.RequestAttributePostProcessor;

//TODO implements aop needed?
public class ExpressionBasedRequestAttributePostProcessor
		implements RequestAttributePostProcessor<MethodInvocation>, AopInfrastructureBean {

	private final MethodSecurityExpressionHandler expressionHandler;
	private final RequestAttributeFactory requestAttributeFactory;

	public ExpressionBasedRequestAttributePostProcessor(MethodSecurityExpressionHandler expressionHandler,
			RequestAttributeFactory requestAttributeFactory) {
		this.expressionHandler = expressionHandler;
		this.requestAttributeFactory = requestAttributeFactory;
	}

	@Override
	public boolean supportsValue(Class<?> clazz) {
		return Expression.class.equals(clazz);
	}

	@Override
	public RequestAttribute process(RequestAttribute attr, MethodInvocation context) {
		Expression expr = (Expression) attr.getValue();
		EvaluationContext evaluationContext = expressionHandler
				.createEvaluationContext(SecurityContextHolder.getContext().getAuthentication(), context);

		return requestAttributeFactory.build(attr.getCategory(), attr.getId(), attr.getDatatype(),
				expr.getValue(evaluationContext));
	}
}
