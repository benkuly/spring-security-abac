package net.folivo.springframework.security.abac.prepost.expression;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.util.Assert;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pep.RequestAttributePostProcessor;
import net.folivo.springframework.security.abac.prepost.MethodInvocationContext;

public class ExpressionBasedRequestAttributePostProcessor
		implements RequestAttributePostProcessor<MethodInvocationContext> {

	private final MethodSecurityExpressionHandler expressionHandler;

	public ExpressionBasedRequestAttributePostProcessor(MethodSecurityExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	@Override
	public boolean supportsValue(Class<?> clazz) {
		return Expression.class.equals(clazz);
	}

	@Override
	public RequestAttribute process(RequestAttribute attr, MethodInvocationContext context) {
		Assert.isAssignable(context.getClass(), MethodInvocationContext.class, "A MethodInvocationContext is needed.");

		Expression expr = (Expression) attr.getValue();
		EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(context.getAuthentication(),
				context.getMethodInvocation());

		return new RequestAttribute(attr.getCategory(), attr.getId(), attr.getDatatype(),
				expr.getValue(evaluationContext));
	}
}
