package net.folivo.springframework.security.abac.demo.abacsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import net.folivo.springframework.security.abac.config.AbacMethodSecurityConfiguration;
import net.folivo.springframework.security.abac.config.PdpConfiguration;
import net.folivo.springframework.security.abac.xacml.core.config.XacmlPdpConfiguration;

@Profile("abacSecurity")
@EnableGlobalMethodSecurity
@Import(XacmlPdpConfiguration.class)
public class AbacSecurityConfig extends AbacMethodSecurityConfiguration {

	@Autowired
	public AbacSecurityConfig(PdpConfiguration<?, ?> pdpConfig) {
		super(pdpConfig);
	}

}
