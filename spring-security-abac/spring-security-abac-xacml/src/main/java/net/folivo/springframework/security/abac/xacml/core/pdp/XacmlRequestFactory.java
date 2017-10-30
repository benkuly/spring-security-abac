package net.folivo.springframework.security.abac.xacml.core.pdp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdMutableRequestAttributes;
import com.att.research.xacml.std.annotations.RequestParser;

import net.folivo.springframework.security.abac.pdp.PdpRequest;
import net.folivo.springframework.security.abac.pdp.PdpRequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.prepost.AttributeCategory;

public class XacmlRequestFactory implements RequestFactory {

	// TODO throw exception when build didn't work
	@Override
	public PdpRequest build(Collection<PdpRequestAttribute> requestAttrs) {
		// TODO use RequestParser from at&t xaxml as reference
		// TODO bad way to use the field when you already know how to get the value
		List<StdMutableRequestAttributes> attributes = new ArrayList<>();
		for (PdpRequestAttribute r : requestAttrs) {
			String datatype = "auto".equals(r.getDatatype()) ? null : r.getDatatype();

			// TODO maybe use identifier base from configuration?
			// TODO maybe set issuer for cloud applications
			try {
				RequestParser.addAttribute(attributes, findCategory(r.getCategory()), new IdentifierImpl(r.getId()),
						false, datatype, null, null, PdpRequestAttribute.class.getField("value"), r);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
					| DataTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		XacmlRequest request = new XacmlRequest();
		for (StdMutableRequestAttributes a : attributes) {
			request.add(a);
		}
		return request;
	}

	private Identifier findCategory(AttributeCategory cat) {
		switch (cat) {
		case SUBJECT:
			return XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT;
		case RESOURCE:
			return XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE;
		case ACTION:
			return XACML3.ID_ATTRIBUTE_CATEGORY_ACTION;
		case ENVIRONMENT:
			// TODO maybe throw exception
		default:
			return XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT;
		}
	}

}
