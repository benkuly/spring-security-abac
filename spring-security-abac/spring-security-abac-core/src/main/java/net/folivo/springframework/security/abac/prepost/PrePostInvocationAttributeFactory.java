package net.folivo.springframework.security.abac.prepost;

import org.springframework.aop.framework.AopInfrastructureBean;

public interface PrePostInvocationAttributeFactory<T> extends AopInfrastructureBean {

	AbacPreInvocationAttribute createPreInvocationAttributes(AttributeCategory category, String id, String datatype,
			T value);

	AbacPostInvocationAttribute createPostInvocationAttributes(AttributeCategory category, String id, String datatype,
			T value);
}
