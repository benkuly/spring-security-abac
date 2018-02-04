package net.folivo.springframework.security.abac.method.aopalliance;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

public class AbacMethodSecurityPointcutAdvisor extends AbstractPointcutAdvisor {

	private final Pointcut pointcut;

	@Override
	public Pointcut getPointcut() {
		return pointcut;
	}

	@Override
	public Advice getAdvice() {
		// TODO Auto-generated method stub
		return null;
	}

}
