package net.folivo.springframework.security.abac.xacml.core.zold;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdMutableRequestAttributes;
import com.att.research.xacml.std.datatypes.DataTypes;

import net.folivo.hapalops.core.service.HapaUserDetails;
import net.folivo.hapalops.core.user.repository.UserRepository;
import net.folivo.spring.xacml.core.AttributeProviderUtil;
import net.folivo.spring.xacml.core.StaticAttributeProvider;

@Component
public class SubjectAttributeProvider implements StaticAttributeProvider {

	private final String PREFIX = "hapalops.core.subject.";
	private final String ID_ID = PREFIX + "id";
	private final String ID_ROLE = PREFIX + "role";
	private final String ID_COMPANY = PREFIX + "company";

	private final UserRepository usRep;

	@Autowired
	public SubjectAttributeProvider(UserRepository usRep) {
		this.usRep = usRep;
	}

	@Override
	public RequestAttributes getAttributes(Authentication authentication, Object resource, String action) {
		// crate empty request attributes
		StdMutableRequestAttributes attributes = new StdMutableRequestAttributes();
		// get user from authentication
		HapaUserDetails user = (HapaUserDetails) authentication.getPrincipal();
		// if user is null (should never be) return the empty object
		if (user != null) {
			AttributeProviderUtil.createAttribute(DataTypes.DT_INTEGER, ID_ID, user.getId()).ifPresent(attributes::add);
			AttributeProviderUtil.createAttribute(DataTypes.DT_STRING, ID_ROLE, user.getRole())
					.ifPresent(attributes::add);
			AttributeProviderUtil.createAttribute(DataTypes.DT_INTEGER, ID_COMPANY, user.getCompanyId())
					.ifPresent(attributes::add);
			// access-subject as attribute-category
			attributes.setCategory(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT);
		}
		return attributes;
	}

}
