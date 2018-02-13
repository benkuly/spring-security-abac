package net.folivo.springframework.security.abac.method.expression;

import java.util.Collection;
import java.util.Collections;

import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;

//TODO implements aop needed?
public class ExpressionBasedRequestAttributePostProcessor
		implements RequestAttributeProcessor<MethodInvocationContext>, AopInfrastructureBean {

	private final MethodSecurityExpressionHandler expressionHandler;

	public ExpressionBasedRequestAttributePostProcessor(MethodSecurityExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	@Override
	public boolean supports(RequestAttribute a) {
		// TODO null check?
		if (String.class.isAssignableFrom(a.getValue().getClass())) {
			String value = (String) a.getValue();
			if (value.startsWith("${") && value.endsWith("}"))
				return true;
		}
		return false;
	}

	@Override
	public Collection<RequestAttribute> process(RequestAttribute attr, MethodInvocationContext context) {
		String value = (String) attr.getValue();
		String expressionString = value.substring(2, value.length() - 1);
		Expression expr = expressionHandler.getExpressionParser().parseExpression(expressionString);

		EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(
				SecurityContextHolder.getContext().getAuthentication(), context.getMethodInvocation());
		context.getReturnedObject().ifPresent(r -> expressionHandler.setReturnObject(r, evaluationContext));
		attr.setValue(expr.getValue(evaluationContext));
		return Collections.singleton(attr);
	}
}
