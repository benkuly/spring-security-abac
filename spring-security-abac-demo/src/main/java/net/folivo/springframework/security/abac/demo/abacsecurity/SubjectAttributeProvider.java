package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.ArrayList;
import java.util.Collection;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.demo.config.AuthenticationUtil;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;

public class SubjectAttributeProvider implements RequestAttributeProvider<MethodInvocationContext> {

	private final RequestAttributeFactory attrFactory;

	public SubjectAttributeProvider(RequestAttributeFactory attrFactory) {
		this.attrFactory = attrFactory;
	}

	@Override
	public Collection<RequestAttribute> getAttributes(MethodInvocationContext context) {
		Collection<RequestAttribute> attrs = new ArrayList<>();
		attrs.add(
				attrFactory.build(AttributeCategory.SUBJECT, "role", AuthenticationUtil.getCurrentLoggedInUserRole()));
		AuthenticationUtil.getCurrentLoggedInUsername()
				.ifPresent(u -> attrs.add(attrFactory.build(AttributeCategory.SUBJECT, "username", u)));
		return attrs;
	}

}
