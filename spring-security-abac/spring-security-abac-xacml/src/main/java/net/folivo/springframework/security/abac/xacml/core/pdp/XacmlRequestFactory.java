package net.folivo.springframework.security.abac.xacml.core.pdp;

import java.util.Collection;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.AttributeFqns;
import org.ow2.authzforce.core.pdp.api.DecisionRequest;
import org.ow2.authzforce.core.pdp.api.DecisionRequestBuilder;
import org.ow2.authzforce.core.pdp.api.PdpEngine;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.DoubleValue;
import org.ow2.authzforce.core.pdp.api.value.IntegerValue;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.core.pdp.api.value.StringValue;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory;

import net.folivo.springframework.security.abac.attributes.RequestAttribute;
import net.folivo.springframework.security.abac.pdp.AttributeCategory;
import net.folivo.springframework.security.abac.pdp.RequestFactory;

public class XacmlRequestFactory implements RequestFactory<DecisionRequest> {

	private static final Log log = LogFactory.getLog(XacmlRequestFactory.class);
	private final PdpEngine pdp;

	public XacmlRequestFactory(PdpEngine pdp) {
		this.pdp = pdp;
	}

	// TODO throw exception when build didn't work
	@Override
	public DecisionRequest build(Collection<RequestAttribute> requestAttrs) {

		final DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(-1, requestAttrs.size());

		for (RequestAttribute r : requestAttrs) {

			if (r.getValue() != null) {
				// TODO maybe use identifier base from configuration?
				// TODO maybe set issuer for cloud applications
				final AttributeFqn attributeId = AttributeFqns.newInstance(findCategory(r.getCategory()),
						Optional.empty(), r.getId());
				// TODO attribute value from java pojo
				final AttributeBag<?> attributeValues = findAttributes(r.getValue());
				requestBuilder.putNamedAttributeIfAbsent(attributeId, attributeValues);
			}
			// TODO that should really never happen
			if (log.isDebugEnabled())
				log.debug("RequestAttribute with id '" + r.getId()
						+ "' will not be used in request because its value is null!");
		}
		return requestBuilder.build(false);
	}

	// TODO i think there will be many issues (not tested) and Value and
	// AttributeDatatype are so weird
	private AttributeBag<?> findAttributes(Object object) {

		if (object instanceof String) {
			return Bags.singletonAttributeBag(StandardDatatypes.STRING, StringValue.parse((String) object));
		} else if (object instanceof Long) { // TODO Integer
			return Bags.singletonAttributeBag(StandardDatatypes.INTEGER, IntegerValue.valueOf((Long) object));
		} else if (object instanceof Boolean) {
			return Bags.singletonAttributeBag(StandardDatatypes.BOOLEAN, BooleanValue.valueOf((Boolean) object));
		} else if (object instanceof Double) { // TODO float
			return Bags.singletonAttributeBag(StandardDatatypes.DOUBLE, new DoubleValue((Double) object));
			// } else if (object instanceof LocalDateTime) {
			//
			// LocalDateTime date = (LocalDateTime) object;
			// GregorianCalendar gcal =
			// GregorianCalendar.from(date.atZone(ZoneId.systemDefault()));
			// try {
			// return Bags.singletonAttributeBag(StandardDatatypes.DATETIME,
			// new
			// DateTimeValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal)));
			// } catch (DatatypeConfigurationException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// } else if (object instanceof URI) {
			// datatype = StandardDatatypes.ANYURI;
			// } else if (object instanceof ISO8601Date) {
			// datatype = StandardDatatypes.DATE;
			// } else if (object instanceof ISO8601Time) {
			// datatypeId = XACML3.ID_DATATYPE_TIME;
			// } else if (object instanceof RFC2396DomainName) {
			// datatypeId = XACML3.ID_DATATYPE_DNSNAME;
			// } else if (object instanceof byte[] || object instanceof HexBinary) {
			// datatypeId = XACML3.ID_DATATYPE_HEXBINARY;
			// } else if (object instanceof Base64Binary) {
			// datatypeId = XACML3.ID_DATATYPE_BASE64BINARY;
			// } else if (object instanceof XPathDayTimeDuration) {
			// datatypeId = XACML3.ID_DATATYPE_DAYTIMEDURATION;
			// } else if (object instanceof IPAddress) {
			// datatypeId = XACML3.ID_DATATYPE_IPADDRESS;
			// } else if (object instanceof RFC822Name) {
			// datatypeId = XACML3.ID_DATATYPE_RFC822NAME;
			// } else if (object instanceof X500Principal) {
			// datatypeId = XACML3.ID_DATATYPE_X500NAME;
			// } else if (object instanceof XPathExpression || object instanceof Node) {
			// datatypeId = XACML3.ID_DATATYPE_XPATHEXPRESSION;
			// } else if (object instanceof XPathYearMonthDuration) {
			// datatypeId = XACML3.ID_DATATYPE_YEARMONTHDURATION;
		}
		log.warn(
				"Cannot decipher java object, defaulting to String datatype. If this is not correct, you must specify the datatype in the annotation.");
		//
		// Default to a string
		//
		return Bags.singletonAttributeBag(StandardDatatypes.STRING, StringValue.parse(object.toString()));
	}

	private String findCategory(AttributeCategory cat) {
		switch (cat) {
		case SUBJECT:
			return XacmlAttributeCategory.XACML_1_0_ACCESS_SUBJECT.value();
		case RESOURCE:
			return XacmlAttributeCategory.XACML_3_0_RESOURCE.value();
		case ACTION:
			return XacmlAttributeCategory.XACML_3_0_ACTION.value();
		case ENVIRONMENT:
			// TODO maybe throw exception for default
		default:
			return XacmlAttributeCategory.XACML_3_0_ENVIRONMENT.value();
		}
	}

}
