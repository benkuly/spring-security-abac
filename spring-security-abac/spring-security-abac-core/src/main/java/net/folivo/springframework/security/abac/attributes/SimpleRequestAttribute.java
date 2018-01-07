package net.folivo.springframework.security.abac.attributes;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;

public class SimpleRequestAttribute implements RequestAttribute {

	private final AttributeCategory category;
	private final String id;
	private Object value;

	public SimpleRequestAttribute(AttributeCategory category, String id, Object value) {
		this.category = category;
		this.id = id;
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
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("{");
		stringBuilder.append("id=");
		stringBuilder.append(id);
		stringBuilder.append(',');

		stringBuilder.append("category=");
		stringBuilder.append(category);
		stringBuilder.append(',');

		stringBuilder.append("value=");
		stringBuilder.append(value);
		stringBuilder.append(',');

		if (value != null) {
			stringBuilder.append("valueClassName=");
			stringBuilder.append(value.getClass().getSimpleName());
			stringBuilder.append(',');
		}

		stringBuilder.append("}");
		return stringBuilder.toString();
	}

}
