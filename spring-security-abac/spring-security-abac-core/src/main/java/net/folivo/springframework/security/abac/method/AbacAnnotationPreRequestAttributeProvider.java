package net.folivo.springframework.security.abac.method;

import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;

//TODO caching
public class AbacAnnotationPreRequestAttributeProvider extends AbacAnnotationRequestAttributeProvider {

	public AbacAnnotationPreRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		super(requestAttributeFactory);
	}

	@Override
	public Stream<RequestAttribute> getAttributes(MethodInvocationContext context) {
		if (context.getMethodInvocation().getMethod().getDeclaringClass() == Object.class) {
			return Stream.empty();
		}

		if (context.getMethodInvocation().getMethod().getDeclaringClass() == Object.class) {
			return Stream.empty();
		}

		// TODO you've already checked this in metadatasource...
		return context.getPreAuthorize()
				.map(abacPreAuthorize -> Streams.concat(
						createRequestAttributes(AttributeCategory.SUBJECT, abacPreAuthorize.subjectAttributes()),
						createRequestAttributes(AttributeCategory.RESOURCE, abacPreAuthorize.resourceAttributes()),
						createRequestAttributes(AttributeCategory.ACTION, abacPreAuthorize.actionAttributes()),
						createRequestAttributes(AttributeCategory.ENVIRONMENT,
								abacPreAuthorize.environmentAttributes())))
				.orElse(Stream.empty());
	}

}
