package net.folivo.springframework.security.abac.prepost.expression;

import org.springframework.expression.ExpressionParser;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.util.Assert;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;

public class ExpressionBasedRequestAttributeFactory implements RequestAttributeFactory {

	private final RequestAttributeFactory defaultFactory;
	private final Object parserLock = new Object();
	private ExpressionParser parser;
	private MethodSecurityExpressionHandler handler;

	public ExpressionBasedRequestAttributeFactory(RequestAttributeFactory defaultFactory,
			MethodSecurityExpressionHandler handler) {
		this.handler = handler;
		this.defaultFactory = defaultFactory;
	}

	@Override
	public RequestAttribute build(AttributeCategory category, String id, String datatype, Object value) {
		Assert.isAssignable(String.class, value.getClass(), "cannot parse expression that is not a string");
		return defaultFactory.build(category, id, datatype, getParser().parseExpression((String) value));
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
