package net.folivo.springframework.security.abac.config;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.Assert;

public class AutoProxyRegistration implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
		if (isProxyTargetClass()) {
			AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
		}
	}

	// TODO
	protected boolean isProxyTargetClass() {
		return false;
	}
}
