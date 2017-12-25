package net.folivo.springframework.security.abac.method.expression;

import java.util.Collection;
import java.util.Collections;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pep.RequestAttributePreProcessor;

//TODO implements aop needed?
public class ExpressionBasedRequestAttributePreProcessor
		implements RequestAttributePreProcessor<MethodInvocation>, AopInfrastructureBean {

	private final Object parserLock = new Object();
	private ExpressionParser parser;
	private MethodSecurityExpressionHandler handler;

	public ExpressionBasedRequestAttributePreProcessor(MethodSecurityExpressionHandler handler) {
		this.handler = handler;
	}

	@Override
	public boolean supportsValue(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public Collection<RequestAttribute> process(RequestAttribute attr, MethodInvocation context) {
		String value = (String) attr.getValue();
		if (value.startsWith("${") && value.endsWith("}")) {
			String expressionString = value.substring(2, value.length() - 1);
			attr.setValue(getParser().parseExpression(expressionString));
		}
		return Collections.singleton(attr);
	}

	// TODO ?
	/**
	 * Delay the lookup of the {@link ExpressionParser} to prevent SEC-2136
	 *
	 * @return
	 */
	private ExpressionParser getParser() {
		if (this.parser != null) {
			return this.parser;
		}
		synchronized (parserLock) {
			this.parser = handler.getExpressionParser();
			this.handler = null;
		}
		return this.parser;
	}
}