package net.folivo.springframework.security.abac.method.expression;

import java.util.Collection;
import java.util.Collections;

import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;

import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pep.RequestAttributePostProcessor;

//TODO implements aop needed?
public class ExpressionBasedRequestAttributeAfterPostProcessor
		implements RequestAttributePostProcessor<MethodInvocationContext>, AopInfrastructureBean {

	private final MethodSecurityExpressionHandler expressionHandler;

	public ExpressionBasedRequestAttributeAfterPostProcessor(MethodSecurityExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	@Override
	public boolean supports(RequestAttribute a) {
		// TODO null check?
		return Expression.class.isAssignableFrom(a.getValue().getClass());
	}

	@Override
	public Collection<RequestAttribute> process(RequestAttribute attr, MethodInvocationContext context) {
		Expression expr = (Expression) attr.getValue();
		EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(
				SecurityContextHolder.getContext().getAuthentication(), context.getMethodInvocation());
		expressionHandler.setReturnObject(context.getReturnedObject(), evaluationContext);
		attr.setValue(expr.getValue(evaluationContext));
		return Collections.singleton(attr);
	}
}
