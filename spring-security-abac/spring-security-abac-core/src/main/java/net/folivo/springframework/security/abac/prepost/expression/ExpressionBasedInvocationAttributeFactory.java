package net.folivo.springframework.security.abac.prepost.expression;

import java.util.Collection;

import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;

import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.prepost.AbacPostInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.AbacPreInvocationAttribute;
import net.folivo.springframework.security.abac.prepost.PrePostInvocationAttributeFactory;

public class ExpressionBasedInvocationAttributeFactory implements PrePostInvocationAttributeFactory {

	@Override
	public PreInvocationAttribute createPreInvocationAttributes(Collection<RequestAttribute> attrs) {
		return new AbacPreInvocationAttribute(attrs);
	}

	@Override
	public PostInvocationAttribute createPostInvocationAttributes(Collection<RequestAttribute> attrs) {
		return new AbacPostInvocationAttribute(attrs);
	}

}
