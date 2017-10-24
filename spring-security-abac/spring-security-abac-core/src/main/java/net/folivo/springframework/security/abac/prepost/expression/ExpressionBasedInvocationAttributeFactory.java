package net.folivo.springframework.security.abac.prepost.expression;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;

import net.folivo.springframework.security.abac.prepost.AbacPostInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.AttributeCategory;
import net.folivo.springframework.security.abac.prepost.PrePostRequestAttributeFactory;

public class ExpressionBasedInvocationAttributeFactory implements PrePostRequestAttributeFactory<String> {

	private final Object parserLock = new Object();
	private ExpressionParser parser;
	private MethodSecurityExpressionHandler handler;

	public ExpressionBasedInvocationAttributeFactory(MethodSecurityExpressionHandler handler) {
		this.handler = handler;
	}

	@Override
	public PreInvocationAttribute createPreInvocationAttributes(AttributeCategory category, String id, String value) {
		try {
			return new AbacPreInvocationAttribute(category, id, getParser().parseExpression(value));
		} catch (ParseException e) {
			throw new IllegalArgumentException("Failed to parse expression '" + e.getExpressionString() + "'", e);
		}
	}

	@Override
	public PostInvocationAttribute createPostInvocationAttributes(AttributeCategory category, String id, String value) {
		try {
			return new AbacPostInvocationAttribute(category, id, getParser().parseExpression(value));
		} catch (ParseException e) {
			throw new IllegalArgumentException("Failed to parse expression '" + e.getExpressionString() + "'", e);
		}
	}

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
