package net.folivo.springframework.security.abac.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;

//TODO caching
public class AbacAnnotationPostRequestAttributeProvider extends AbacAnnotationRequestAttributeProvider {

	public AbacAnnotationPostRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		super(requestAttributeFactory);
	}

	@Override
	public Collection<RequestAttribute> getAttributes(MethodInvocationContext context) {
		if (context.getMethodInvocation().getMethod().getDeclaringClass() == Object.class) {
			return Collections.emptyList();
		}

		AbacPostAuthorize abacPostAuthorize = AbacAnnotationUtil.findAnnotation(context.getMethodInvocation(),
				AbacPostAuthorize.class);

		if (abacPostAuthorize == null) {
			// There is no meta-data so return
			return Collections.emptyList();
		}

		ArrayList<RequestAttribute> attrs = new ArrayList<>();

		if (abacPostAuthorize != null) {
			createAndAddRequestAttribute(AttributeCategory.SUBJECT, abacPostAuthorize.subjectAttributes(), attrs);
			createAndAddRequestAttribute(AttributeCategory.RESOURCE, abacPostAuthorize.resourceAttributes(), attrs);
			createAndAddRequestAttribute(AttributeCategory.ACTION, abacPostAuthorize.actionAttributes(), attrs);
			createAndAddRequestAttribute(AttributeCategory.ENVIRONMENT, abacPostAuthorize.environmentAttributes(),
					attrs);
		}

		attrs.trimToSize();

		return attrs;
	}

}
