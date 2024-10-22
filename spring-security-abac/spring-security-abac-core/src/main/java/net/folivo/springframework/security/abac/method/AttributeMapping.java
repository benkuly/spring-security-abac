package net.folivo.springframework.security.abac.method;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AttributeMapping {
	public String id();

	public String value();
}
