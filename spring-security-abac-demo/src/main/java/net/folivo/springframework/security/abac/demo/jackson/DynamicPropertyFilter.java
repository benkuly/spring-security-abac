package net.folivo.springframework.security.abac.demo.jackson;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

@Component
public class DynamicPropertyFilter extends SimpleBeanPropertyFilter {

	private final Collection<PropertyFilter> filters;

	public DynamicPropertyFilter(Collection<PropertyFilter> filters) {
		this.filters = filters;
	}

	@Override
	public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
			throws Exception {
		if (include(writer)) {
			for (PropertyFilter f : filters) {
				if (f.supports(pojo.getClass()) && f.serialize(pojo, writer))
					writer.serializeAsField(pojo, jgen, provider);
				return;
			}
		} else if (!jgen.canOmitFields()) { // since 2.3
			// TODO ?
			writer.serializeAsOmittedField(pojo, jgen, provider);
		}
	}

}
