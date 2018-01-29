package net.folivo.springframework.security.abac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.contexthandler.PdpClient;
import net.folivo.springframework.security.abac.contexthandler.RequestContextHandler;
import net.folivo.springframework.security.abac.contexthandler.RequestFactory;
import net.folivo.springframework.security.abac.contexthandler.ResponseEvaluator;

@Configuration
public interface PdpConfiguration<R, S, T> {

	@Bean
	PdpClient<R, S> pdpClient();

	@Bean
	RequestFactory<R> requestFactory();

	@Bean
	ResponseEvaluator<S> responseEvaluator();

	@Bean
	RequestAttributeFactory requestAttributeFactory();

	@Bean
	RequestContextHandler<T> requestContextHandler();

}
