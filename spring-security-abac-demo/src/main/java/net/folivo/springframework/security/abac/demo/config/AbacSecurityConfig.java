package net.folivo.springframework.security.abac.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.folivo.springframework.security.abac.config.AbacMethodSecurityConfiguration;

@Configuration
@Import(AbacMethodSecurityConfiguration.class)
public class AbacSecurityConfig {

}
