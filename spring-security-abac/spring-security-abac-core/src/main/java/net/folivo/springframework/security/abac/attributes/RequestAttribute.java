package net.folivo.springframework.security.abac.attributes;

public interface RequestAttribute {

	public RequestAttributeMetadata getMetadata();

	public Object getValue();

	// TODO hm :/ maybe extra RequestAttributeValue class
	public void setValue(Object value);

}
