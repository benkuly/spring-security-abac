package net.folivo.springframework.security.abac.method.aopalliance;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

public class AbacMethodSecurityPointcutAdvisor extends AbstractPointcutAdvisor {

	private final Pointcut pointcut;
	private final transient MethodInterceptor interceptor;

	public AbacMethodSecurityPointcutAdvisor(Pointcut pointcut, MethodInterceptor interceptor) {
		this.pointcut = pointcut;
		this.interceptor = interceptor;
	}

	@Override
	public Pointcut getPointcut() {
		return pointcut;
	}

	@Override
	public Advice getAdvice() {
		return interceptor;
	}

}
