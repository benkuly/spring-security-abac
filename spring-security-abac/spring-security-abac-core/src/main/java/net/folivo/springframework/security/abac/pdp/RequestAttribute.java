package net.folivo.springframework.security.abac.pdp;

public interface RequestAttribute {

	public AttributeCategory getCategory();

	public String getId();

	public String getDatatype();

	public Object getValue();

	public void setValue(Object value);

}
