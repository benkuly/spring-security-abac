package net.folivo.springframework.security.abac.xacml.core.config;

import org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory;

import net.folivo.springframework.security.abac.attributes.AttributeCategory;

public final class AbacUtils {
	public static String mapCategory(AttributeCategory cat) {
		switch (cat) {
		case SUBJECT:
			return XacmlAttributeCategory.XACML_1_0_ACCESS_SUBJECT.value();
		case RESOURCE:
			return XacmlAttributeCategory.XACML_3_0_RESOURCE.value();
		case ACTION:
			return XacmlAttributeCategory.XACML_3_0_ACTION.value();
		case ENVIRONMENT:
			// TODO maybe throw exception for default
		default:
			return XacmlAttributeCategory.XACML_3_0_ENVIRONMENT.value();
		}
	}

	public static AttributeCategory mapCategory(String cat) {
		if (cat.equals(XacmlAttributeCategory.XACML_1_0_ACCESS_SUBJECT.value()))
			return AttributeCategory.SUBJECT;
		else if (cat.equals(XacmlAttributeCategory.XACML_3_0_RESOURCE.value()))
			return AttributeCategory.RESOURCE;
		else if (cat.equals(XacmlAttributeCategory.XACML_3_0_ACTION.value()))
			return AttributeCategory.ACTION;
		else
			// TODO maybe throw exception for default
			return AttributeCategory.ENVIRONMENT;

	}
}
