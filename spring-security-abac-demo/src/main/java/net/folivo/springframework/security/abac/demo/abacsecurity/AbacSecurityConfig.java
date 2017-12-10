package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import net.folivo.springframework.security.abac.config.AbacMethodSecurityConfiguration;
import net.folivo.springframework.security.abac.demo.config.AuthenticationUtil;
import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;
import net.folivo.springframework.security.abac.pep.RequestAttributeProvider;
import net.folivo.springframework.security.abac.xacml.core.config.XacmlPdpConfiguration;

@Profile("abacSecurity")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({ XacmlPdpConfiguration.class })
public class AbacSecurityConfig extends AbacMethodSecurityConfiguration {

	@Bean
	@Override
	public List<RequestAttributeProvider<MethodInvocation>> requestAttributeProvider() {
		List<RequestAttributeProvider<MethodInvocation>> providers = super.requestAttributeProvider();
		providers.add(subjProvider());
		return providers;
	}

	@Bean
	public SubjectAttributeProvider subjProvider() {
		return new SubjectAttributeProvider(requestAttributeFactory());
	}

	class SubjectAttributeProvider implements RequestAttributeProvider<MethodInvocation> {

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
}
