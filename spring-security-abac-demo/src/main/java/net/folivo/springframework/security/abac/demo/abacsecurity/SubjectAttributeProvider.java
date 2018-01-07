package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.ArrayList;
import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.demo.config.AuthenticationUtil;
import net.folivo.springframework.security.abac.pdp.AttributeCategory;

public class SubjectAttributeProvider implements RequestAttributeProvider<MethodInvocation> {

	private final RequestAttributeFactory attrFactory;

	public SubjectAttributeProvider(RequestAttributeFactory attrFactory) {
		this.attrFactory = attrFactory;
	}

	@Override
	public Collection<RequestAttribute> getAttributes(MethodInvocation context) {
		Collection<RequestAttribute> attrs = new ArrayList<>();
		attrs.add(
				attrFactory.build(AttributeCategory.SUBJECT, "role", AuthenticationUtil.getCurrentLoggedInUserRole()));
		attrs.add(attrFactory.build(AttributeCategory.SUBJECT, "username",
				AuthenticationUtil.getCurrentLoggedInUsername().orElse(null)));
		// provider sollten unabhängig von expressions sein!
		return attrs;
	}

}
