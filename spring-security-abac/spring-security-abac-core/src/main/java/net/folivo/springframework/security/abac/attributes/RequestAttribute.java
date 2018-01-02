package net.folivo.springframework.security.abac.attributes;

public interface RequestAttribute extends RequestAttributeMetadata {

	public Object getValue();

	// TODO hm :/ mybe extra RequestAttributeValue class
	public void setValue(Object value);

}
