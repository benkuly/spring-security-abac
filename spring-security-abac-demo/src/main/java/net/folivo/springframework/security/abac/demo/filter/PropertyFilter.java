package net.folivo.springframework.security.abac.demo.filter;

import com.fasterxml.jackson.databind.ser.PropertyWriter;

public interface PropertyFilter {

	boolean supports(Class<?> clazz);

	boolean serialize(Object pojo, PropertyWriter writer);

}
