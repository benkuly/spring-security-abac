package net.folivo.springframework.security.abac.method;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.aop.framework.AopInfrastructureBean;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;

//TODO implements aop needed?
//TODO caching
public abstract class AbacAnnotationRequestAttributeProvider
		implements RequestAttributeProvider<MethodInvocationContext>, AopInfrastructureBean {

	private final RequestAttributeFactory requestAttributeFactory;

	public AbacAnnotationRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		this.requestAttributeFactory = requestAttributeFactory;
	}

	protected Stream<RequestAttribute> createRequestAttributes(AttributeCategory category,
			AttributeMapping[] attributes) {
		return Arrays.asList(attributes).stream().map(a -> requestAttributeFactory.build(category, a.id(), a.value()));
	}

}
