package net.folivo.springframework.security.abac.method.aopalliance;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

public class AbacMethodSecurityPointcut extends StaticMethodMatcherPointcut implements Serializable {

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		// TODO ask provider supports
	}

}
