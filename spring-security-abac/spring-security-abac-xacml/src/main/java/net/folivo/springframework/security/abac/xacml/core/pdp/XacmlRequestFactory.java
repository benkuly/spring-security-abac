package net.folivo.springframework.security.abac.xacml.core.pdp;

import java.util.Collection;

import com.att.research.xacml.api.DataTypeFactory;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdMutableAttribute;

import net.folivo.springframework.security.abac.pdp.Request;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestFactory;

public class XacmlRequestFactory implements RequestFactory {

	@Override
	public Request build(Collection<RequestAttribute> requestAttrs) {
		for (RequestAttribute r:requestAttrs) {
			Class<?> type=r.getValue().getClass();
			StdAttributeValue<?> value=new StdAttributeValue<?>(DataTypeFactory., valueIn)
		}
		StdMutableAttribute attr = new StdMutableAttribute();
		
		return null;
	}

}
