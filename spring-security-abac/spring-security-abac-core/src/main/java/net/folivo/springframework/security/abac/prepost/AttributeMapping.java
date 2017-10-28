package net.folivo.springframework.security.abac.prepost;

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

	public String datatype() default "auto";
}
