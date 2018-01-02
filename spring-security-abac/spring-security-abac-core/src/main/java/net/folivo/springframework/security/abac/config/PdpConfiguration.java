package net.folivo.springframework.security.abac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;
import net.folivo.springframework.security.abac.pep.PepEngine;

@Configuration
public interface PdpConfiguration<R, S> {

	@Bean
	PdpClient<R, S> pdpClient();

	@Bean
	RequestFactory<R> requestFactory();

	@Bean
	ResponseEvaluator<S> responseEvaluator();

	@Bean
	RequestAttributeFactory requestAttributeFactory();

	@Bean
	PepEngine pepEngine();

}
