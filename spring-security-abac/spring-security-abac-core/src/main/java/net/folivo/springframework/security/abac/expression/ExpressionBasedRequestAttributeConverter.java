package net.folivo.springframework.security.abac.expression;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeConverter;

public class ExpressionBasedRequestAttributeConverter implements RequestAttributeConverter {

	private final MethodSecurityExpressionHandler expressionHandler;

	public ExpressionBasedRequestAttributeConverter(MethodSecurityExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	@Override
	public boolean supportsValueType(Class<?> clazz) {
		return Expression.class.equals(clazz);
	}

	@Override
	public RequestAttribute convert(RequestAttribute requestAttr, Authentication authentication,
			MethodInvocation method) {
		Expression expr = (Expression) requestAttr.getValue();
		EvaluationContext context = expressionHandler.createEvaluationContext(authentication, method);

		return new RequestAttribute(requestAttr.getCategory(), requestAttr.getId(), expr.getValue(context));
	}

}
