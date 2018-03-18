package net.folivo.springframework.security.abac.expression;

import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProcessor;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;

//TODO implements aop needed?
public class ExpressionBasedRequestAttributeProcessor
		implements RequestAttributeProcessor<MethodInvocationContext>, AopInfrastructureBean {

	private final static Log LOG = LogFactory.getLog(ExpressionBasedRequestAttributeProcessor.class);
	private final MethodSecurityExpressionHandler expressionHandler;

	public ExpressionBasedRequestAttributeProcessor(MethodSecurityExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	@Override
	public boolean supports(RequestAttribute a) {
		Object value = a.getValue();
		if (value == null) {
			if (LOG.isDebugEnabled())
				LOG.debug("RequestAttribute " + a + " will not be processed because its value is null!");
			return false;
		}
		if (String.class.isAssignableFrom(a.getValue().getClass())) {
			String stringValue = (String) a.getValue();
			if (stringValue.startsWith("${") && stringValue.endsWith("}"))
				return true;
		}
		return false;
	}

	@Override
	public Stream<RequestAttribute> process(RequestAttribute attr, MethodInvocationContext context) {
		String value = (String) attr.getValue();
		String expressionString = value.substring(2, value.length() - 1);
		Expression expr = expressionHandler.getExpressionParser().parseExpression(expressionString);

		EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(
				SecurityContextHolder.getContext().getAuthentication(), context.getMethodInvocation());
		context.getReturnedObject().ifPresent(r -> expressionHandler.setReturnObject(r, evaluationContext));
		attr.setValue(expr.getValue(evaluationContext));
		return Stream.of(attr);
	}
}
