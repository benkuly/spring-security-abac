package net.folivo.springframework.security.abac.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern("/browser/**")) {
			registry.addResourceHandler("/browser/**")
					.addResourceLocations("classpath:/META-INF/resources/webjars/hal-browser/");
		}
	}

}
