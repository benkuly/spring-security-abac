package net.folivo.springframework.security.abac.expression;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;

import net.folivo.springframework.security.abac.pdp.PdpRequestAttribute;
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
	public PdpRequestAttribute convert(PdpRequestAttribute requestAttr, Authentication authentication,
			MethodInvocation method) {
		Expression expr = (Expression) requestAttr.getValue();
		EvaluationContext context = expressionHandler.createEvaluationContext(authentication, method);

		return new PdpRequestAttribute(requestAttr.getCategory(), requestAttr.getId(), requestAttr.getDatatype(),
				expr.getValue(context));
	}
}
