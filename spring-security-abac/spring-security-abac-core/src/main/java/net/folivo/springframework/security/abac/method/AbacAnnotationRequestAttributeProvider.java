package net.folivo.springframework.security.abac.method;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopInfrastructureBean;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.pdp.AttributeCategory;

//TODO implements aop needed?
//TODO caching
public abstract class AbacAnnotationRequestAttributeProvider
		implements RequestAttributeProvider<MethodInvocation>, AopInfrastructureBean {

	private final RequestAttributeFactory requestAttributeFactory;

	public AbacAnnotationRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		this.requestAttributeFactory = requestAttributeFactory;
	}

	protected abstract Collection<RequestAttribute> getAttributes(Method method, Class<?> targetClass);

	protected void createAndAddRequestAttribute(AttributeCategory category, AttributeMapping[] attributes,
			List<RequestAttribute> listToAdd) {
		Arrays.asList(attributes).stream().map(a -> requestAttributeFactory.build(category, a.id(), a.value()))
				.forEach(listToAdd::add);
	}

	@Override
	public Collection<RequestAttribute> getAttributes(MethodInvocation mi) {
		return AbacAnnotationUtil.callMethod(mi, this::getAttributes);
	}

}
