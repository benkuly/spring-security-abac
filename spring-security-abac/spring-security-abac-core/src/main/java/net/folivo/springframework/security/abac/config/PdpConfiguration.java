package net.folivo.springframework.security.abac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.contexthandler.PdpClient;
import net.folivo.springframework.security.abac.contexthandler.PdpRequestFactory;
import net.folivo.springframework.security.abac.contexthandler.RequestContextHandler;
import net.folivo.springframework.security.abac.pep.PepResponseFactory;

@Configuration
public interface PdpConfiguration<R, S, T> {

	@Bean
	PdpClient<R, S, T> pdpClient();

	@Bean
	PdpRequestFactory<R> pdpRequestFactory();

	@Bean
	PepResponseFactory<S> pepResponseFactory();

	@Bean
	RequestAttributeFactory requestAttributeFactory();

	@Bean
	RequestContextHandler<T> requestContextHandler();

}
