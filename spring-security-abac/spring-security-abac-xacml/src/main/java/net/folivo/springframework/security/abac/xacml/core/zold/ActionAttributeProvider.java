package net.folivo.springframework.security.abac.xacml.core.zold;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdMutableRequestAttributes;
import com.att.research.xacml.std.datatypes.DataTypes;

import net.folivo.spring.xacml.core.AttributeProviderUtil;
import net.folivo.spring.xacml.core.StaticAttributeProvider;

@Component
public class ActionAttributeProvider implements StaticAttributeProvider {

	private final String PREFIX = "hapalops.core.action.";

	@Override
	public RequestAttributes getAttributes(Authentication authentication, Object resource, String action) {
		StdMutableRequestAttributes attributes = new StdMutableRequestAttributes();

		AttributeProviderUtil.createAttribute(DataTypes.DT_STRING, PREFIX + "actionId", action)
				.ifPresent(attributes::add);
		AttributeProviderUtil.createAttribute(DataTypes.DT_STRING, PREFIX + "classType", resource.getClass().getName())
				.ifPresent(attributes::add);

		// action as attribute-category
		attributes.setCategory(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION);
		return attributes;
	}

}
