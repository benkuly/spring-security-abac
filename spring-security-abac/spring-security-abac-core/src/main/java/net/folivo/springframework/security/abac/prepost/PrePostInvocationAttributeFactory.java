package net.folivo.springframework.security.abac.prepost;

import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;

public interface PrePostInvocationAttributeFactory<T> extends AopInfrastructureBean {

	PreInvocationAttribute createPreInvocationAttributes(AttributeCategory category, String id, String datatype,
			T value);

	PostInvocationAttribute createPostInvocationAttributes(AttributeCategory category, String id, String datatype,
			T value);
}
