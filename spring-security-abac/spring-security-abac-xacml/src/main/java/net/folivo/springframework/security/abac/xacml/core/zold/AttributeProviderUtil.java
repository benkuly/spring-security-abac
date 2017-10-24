package net.folivo.springframework.security.abac.xacml.core.zold;

import java.util.Optional;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.DataType;
import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdMutableAttribute;

//TODO testen
public class AttributeProviderUtil {

	public static Optional<Attribute> createAttribute(DataType<?> dataType, String identifier, Object value) {
		try {
			return Optional.of(new StdMutableAttribute(null, new IdentifierImpl(identifier),
					dataType.createAttributeValue(value)));
		} catch (DataTypeException e) {
			return Optional.empty();
		}
	}

}
