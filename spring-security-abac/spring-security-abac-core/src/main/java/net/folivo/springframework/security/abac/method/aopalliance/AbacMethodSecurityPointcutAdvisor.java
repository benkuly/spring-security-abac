package net.folivo.springframework.security.abac.method.aopalliance;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

public class AbacMethodSecurityPointcutAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

	private final Pointcut pointcut;
	private final String methodInterceptorBeanName;
	private transient MethodInterceptor interceptor;
	private transient volatile Object adviceMonitor = new Object();
	private BeanFactory beanFactory;

	public AbacMethodSecurityPointcutAdvisor(Pointcut pointcut, String methodInterceptorBeanName) {
		this.pointcut = pointcut;
		this.methodInterceptorBeanName = methodInterceptorBeanName;
	}

	@Override
	public Pointcut getPointcut() {
		return pointcut;
	}

	@Override
	public Advice getAdvice() {
		synchronized (this.adviceMonitor) {
			if (interceptor == null) {
				Assert.notNull(methodInterceptorBeanName,
						"'methodInterceptorBeanName' must be set for use with bean factory lookup.");
				Assert.state(beanFactory != null, "BeanFactory must be set to resolve 'adviceBeanName'");
				interceptor = beanFactory.getBean(methodInterceptorBeanName, MethodInterceptor.class);
			}
			return interceptor;
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
