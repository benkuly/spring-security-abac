package net.folivo.springframework.security.abac.prepost;

import java.util.Collection;

import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;

public interface PrePostInvocationAttributeFactory extends AopInfrastructureBean {

	PreInvocationAttribute createPreInvocationAttributes(Collection<RequestAttribute> attrs);

	PostInvocationAttribute createPostInvocationAttributes(Collection<RequestAttribute> attrs);
}
