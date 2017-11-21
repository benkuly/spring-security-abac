package net.folivo.springframework.security.abac.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;
import net.folivo.springframework.security.abac.demo.jackson.DynamicPropertyFilter;
import net.folivo.springframework.security.abac.demo.jackson.PostingDeserializer;
import net.folivo.springframework.security.abac.demo.jackson.PostingSerializer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final DynamicPropertyFilter dynamicFilter;
	private final StdUserRepository repo;

	public WebConfig(DynamicPropertyFilter dynamicFilter, StdUserRepository repo) {
		this.dynamicFilter = dynamicFilter;
		this.repo = repo;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper());

		converters.add(converter);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder.json()
				.filters(new SimpleFilterProvider().addFilter("dynamicFilter", dynamicFilter))
				.serializers(new PostingSerializer()).deserializers(new PostingDeserializer(repo)).build();
	}

}
