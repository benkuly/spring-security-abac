package net.folivo.springframework.security.abac.method.aopalliance;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import net.folivo.springframework.security.abac.method.AbacAnnotationUtil;
import net.folivo.springframework.security.abac.method.AbacPostAuthorize;
import net.folivo.springframework.security.abac.method.AbacPreAuthorize;

public class AbacMethodSecurityPointcut extends StaticMethodMatcherPointcut implements Serializable {

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return AbacAnnotationUtil.findAnnotation(method, targetClass, AbacPreAuthorize.class).isPresent()
				|| AbacAnnotationUtil.findAnnotation(method, targetClass, AbacPostAuthorize.class).isPresent();
	}

}
