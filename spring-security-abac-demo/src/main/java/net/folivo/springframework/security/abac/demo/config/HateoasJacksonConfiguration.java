package net.folivo.springframework.security.abac.demo.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import net.folivo.springframework.security.abac.demo.filter.DynamicPropertyFilter;

//This class is needed because Spring HATEOAS has NO Configuration Beans. Thanks Oliver Gierke.
@Configuration
public class HateoasJacksonConfiguration implements BeanPostProcessor {

	private static final String HAL_OBJECT_MAPPER_BEAN_NAME = "_halObjectMapper";

	private final DynamicPropertyFilter dynamicFilter;

	@Autowired
	public HateoasJacksonConfiguration(DynamicPropertyFilter dynamicFilter) {
		this.dynamicFilter = dynamicFilter;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		if (!HAL_OBJECT_MAPPER_BEAN_NAME.equals(beanName)) {
			return bean;
		}

		ObjectMapper mapper = (ObjectMapper) bean;
		mapper.setFilterProvider(new SimpleFilterProvider().addFilter("dynamicFilter", dynamicFilter));

		return mapper;
	}
}
