package net.folivo.springframework.security.abac.xacml.core.pdp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdMutableRequest;
import com.att.research.xacml.std.StdMutableRequestAttributes;
import com.att.research.xacml.std.annotations.RequestParser;

import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.RequestFactory;

public class XacmlRequestFactory implements RequestFactory<Request> {

	private static final Log log = LogFactory.getLog(XacmlRequestFactory.class);

	// TODO throw exception when build didn't work
	@Override
	public Request build(Collection<RequestAttribute> requestAttrs) {
		// TODO use RequestParser from at&t xaxml as reference
		// TODO bad way to use the field when you already know how to get the value
		List<StdMutableRequestAttributes> attributes = new ArrayList<>();
		for (RequestAttribute r : requestAttrs) {
			log.debug(r);
			String datatype = "auto".equals(r.getDatatype()) ? null : r.getDatatype();

			// TODO maybe use identifier base from configuration?
			// TODO maybe set issuer for cloud applications
			try {
				RequestParser.addAttribute(attributes, findCategory(r.getCategory()), new IdentifierImpl(r.getId()),
						false, datatype, null, null, ReflectionUtils.findField(r.getClass(), "value"), r);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StdMutableRequest request = new StdMutableRequest();
		attributes.stream().forEach(request::add);
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
			// TODO maybe throw exception for default
		default:
			return XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT;
		}
	}

}
