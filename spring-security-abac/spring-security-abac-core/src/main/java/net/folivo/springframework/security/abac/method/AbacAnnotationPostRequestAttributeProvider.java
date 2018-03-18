package net.folivo.springframework.security.abac.method;

import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;

//TODO caching
public class AbacAnnotationPostRequestAttributeProvider extends AbacAnnotationRequestAttributeProvider {

	public AbacAnnotationPostRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		super(requestAttributeFactory);
	}

	@Override
	public Stream<RequestAttribute> getAttributes(MethodInvocationContext context) {
		if (context.getMethodInvocation().getMethod().getDeclaringClass() == Object.class) {
			return Stream.empty();
		}

		// TODO you've already checked this in metadatasource...
		return context.getPostAuthorize()
				.map(abacPostAuthorize -> Streams.concat(
						createRequestAttributes(AttributeCategory.SUBJECT, abacPostAuthorize.subjectAttributes()),
						createRequestAttributes(AttributeCategory.RESOURCE, abacPostAuthorize.resourceAttributes()),
						createRequestAttributes(AttributeCategory.ACTION, abacPostAuthorize.actionAttributes()),
						createRequestAttributes(AttributeCategory.ENVIRONMENT,
								abacPostAuthorize.environmentAttributes())))
				.orElse(Stream.empty());
	}
}
