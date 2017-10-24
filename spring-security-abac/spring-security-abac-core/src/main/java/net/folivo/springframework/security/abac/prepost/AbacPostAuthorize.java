package net.folivo.springframework.security.abac.prepost;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AbacPostAuthorize {

	AttributeMapping[] subjectAttributes() default {};

	AttributeMapping[] resourceAttributes() default {};

	AttributeMapping[] actionAttributes() default {};

	AttributeMapping[] environmentAttributes() default {};
}
