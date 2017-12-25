package net.folivo.springframework.security.abac.method;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;

//TODO caching
public class AbacAnnotationPreRequestAttributeProvider extends AbacAnnotationRequestAttributeProvider {

	public AbacAnnotationPreRequestAttributeProvider(RequestAttributeFactory requestAttributeFactory) {
		super(requestAttributeFactory);
	}

	@Override
	protected Collection<RequestAttribute> getAttributes(Method method, Class<?> targetClass) {
		if (method.getDeclaringClass() == Object.class) {
			return Collections.emptyList();
		}

		AbacPreAuthorize abacPreAuthorize = AbacAnnotationUtil.findAnnotation(method, targetClass,
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