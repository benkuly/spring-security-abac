package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.ArrayList;
import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;

import net.folivo.springframework.security.abac.demo.config.AuthenticationUtil;
import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;
import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;

public class SubjectAttributeProvider implements RequestAttributeProvider<MethodInvocation> {

	private final RequestAttributeFactory attrFactory;

	public SubjectAttributeProvider(RequestAttributeFactory attrFactory) {
		this.attrFactory = attrFactory;
	}

	@Override
	public Collection<RequestAttribute> getAttributes(MethodInvocation context) {
		Collection<RequestAttribute> attrs = new ArrayList<>();
		attrs.add(attrFactory.build(AttributeCategory.SUBJECT, "role", "auto",
				AuthenticationUtil.getCurrentLoggedInUserRole()));
		attrs.add(attrFactory.build(AttributeCategory.SUBJECT, "username", "auto",
				AuthenticationUtil.getCurrentLoggedInUsername().orElse(null)));
		// provider sollten unabhängig von expressions sein!
		return attrs;
	}

}
