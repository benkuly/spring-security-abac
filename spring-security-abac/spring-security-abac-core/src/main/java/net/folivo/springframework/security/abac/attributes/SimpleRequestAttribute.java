package net.folivo.springframework.security.abac.attributes;

import org.springframework.util.Assert;

public class SimpleRequestAttribute implements RequestAttribute {

	private final RequestAttributeMetadata metadata;
	private Object value;

	public SimpleRequestAttribute(RequestAttributeMetadata metadata, Object value) {
		Assert.notNull(metadata, "Metadata must not be null!");
		this.metadata = metadata;
		this.value = value;
	}

	@Override
	public RequestAttributeMetadata getMetadata() {
		return metadata;
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
		stringBuilder.append("metadata=");
		stringBuilder.append(metadata);
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
