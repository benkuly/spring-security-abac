package net.folivo.springframework.security.abac.attributes;

public class SimpleRequestAttributeMetadata implements RequestAttributeMetadata {

	private final AttributeCategory category;
	private final String id;

	public SimpleRequestAttributeMetadata(AttributeCategory category, String id) {
		this.category = category;
		this.id = id;
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
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("{");
		stringBuilder.append("id=");
		stringBuilder.append(id);
		stringBuilder.append(',');

		stringBuilder.append("category=");
		stringBuilder.append(category);
		stringBuilder.append(',');

		stringBuilder.append("}");
		return stringBuilder.toString();
	}

}
