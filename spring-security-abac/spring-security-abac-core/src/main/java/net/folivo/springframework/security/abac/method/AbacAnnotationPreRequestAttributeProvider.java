package net.folivo.springframework.security.abac.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;
import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;

//TODO caching
public class AbacAnnotationPreRequestAttributeProvider extends AbacAnnotationRequestAttributeProvider {

	public AbacAnnotationPreRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		super(requestAttributeFactory);
	}

	@Override
	public Collection<RequestAttribute> getAttributes(MethodInvocationContext context) {
		if (context.getMethodInvocation().getMethod().getDeclaringClass() == Object.class) {
			return Collections.emptyList();
		}

		AbacPreAuthorize abacPreAuthorize = AbacAnnotationUtil.findAnnotation(context.getMethodInvocation(),
				AbacPreAuthorize.class);

		if (abacPreAuthorize == null) {
			// There is no meta-data so return
			return Collections.emptyList();
		}

		ArrayList<RequestAttribute> attrs = new ArrayList<>();

		if (abacPreAuthorize != null) {
			createAndAddRequestAttribute(AttributeCategory.SUBJECT, abacPreAuthorize.subjectAttributes(), attrs);
			createAndAddRequestAttribute(AttributeCategory.RESOURCE, abacPreAuthorize.resourceAttributes(), attrs);
			createAndAddRequestAttribute(AttributeCategory.ACTION, abacPreAuthorize.actionAttributes(), attrs);
			createAndAddRequestAttribute(AttributeCategory.ENVIRONMENT, abacPreAuthorize.environmentAttributes(),
					attrs);
		}

		attrs.trimToSize();

		return attrs;
	}

}
