package net.folivo.springframework.security.abac.xacml.core.pdp;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;

public class XacmlRequestAttribute implements RequestAttribute {

	private final AttributeCategory category;
	private final String id;
	private final String datatype;
	private final Object value;

	public XacmlRequestAttribute(AttributeCategory category, String id, String datatype, Object value) {
		this.category = category;
		this.id = id;
		this.datatype = datatype;
		this.value = value;
	}

	@Override
	public AttributeCategory getCategory() {
		return category;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getDatatype() {
		return datatype;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("{");
		stringBuilder.append("id=");
		stringBuilder.append(id.toString());
		stringBuilder.append(',');

		stringBuilder.append("category=");
		stringBuilder.append(category.toString());
		stringBuilder.append(',');

		stringBuilder.append("value=");
		stringBuilder.append(value.toString());
		stringBuilder.append(',');

		stringBuilder.append("valueClassName=");
		stringBuilder.append(value.getClass().getSimpleName());
		stringBuilder.append(',');

		stringBuilder.append("datatype=");
		stringBuilder.append(datatype.toString());

		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}
