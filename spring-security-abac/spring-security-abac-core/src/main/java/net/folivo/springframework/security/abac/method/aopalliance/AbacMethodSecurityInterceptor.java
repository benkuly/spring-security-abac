package net.folivo.springframework.security.abac.method.aopalliance;

import java.util.Optional;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;

import net.folivo.springframework.security.abac.method.AbacAnnotationUtil;
import net.folivo.springframework.security.abac.method.AbacPostAuthorize;
import net.folivo.springframework.security.abac.method.AbacPreAuthorize;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;

public class AbacMethodSecurityInterceptor extends AbstractSecurityInterceptor implements MethodInterceptor {

	private SecurityMetadataSource securityMetadataSource;

	@Override
	public Class<?> getSecureObjectClass() {
		return MethodInvocation.class;
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		boolean preAuthorize = AbacAnnotationUtil.findAnnotation(mi, AbacPreAuthorize.class) != null;
		boolean postAuthorize = AbacAnnotationUtil.findAnnotation(mi, AbacPostAuthorize.class) != null;
		MethodInvocationContext context = new MethodInvocationContext(mi, Optional.empty(), preAuthorize,
				postAuthorize);

		InterceptorStatusToken token = super.beforeInvocation(context);

		Object result;
		try {
			result = mi.proceed();
		} finally {
			super.finallyInvocation(token);
		}
		context.setReturnedObject(Optional.of(result));
		return super.afterInvocation(token, result);
	}

	@Override
	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return this.securityMetadataSource;
	}

	public SecurityMetadataSource getSecurityMetadataSource() {
		return securityMetadataSource;
	}

	public void setSecurityMetadataSource(SecurityMetadataSource securityMetadataSource) {
		this.securityMetadataSource = securityMetadataSource;
	}

}
