package net.folivo.springframework.security.abac.xacml.core.zold;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdMutableRequestAttributes;
import com.att.research.xacml.std.datatypes.DataTypes;

import net.folivo.hapalops.core.security.Action;
import net.folivo.hapalops.core.user.model.User;
import net.folivo.hapalops.core.user.repository.RelationRepository;
import net.folivo.spring.xacml.core.AttributeProviderUtil;
import net.folivo.spring.xacml.core.ResourceAttributeProvider;

@Component
public class UserAttributeProvider implements ResourceAttributeProvider {

	private final String PREFIX = "hapalops.core.resource.user.";
	private final String ID_ROLE = PREFIX + "role";
	private final String ID_COMPANY = PREFIX + "company";

	private final RelationRepository reRep;

	@Autowired
	public UserAttributeProvider(RelationRepository reRep) {
		this.reRep = reRep;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		if (User.class.equals(clazz))
			return true;
		return false;
	}

	@Override
	public RequestAttributes getAttributes(Authentication authentication, Object resource, String action) {
		User user = (User) resource;
		StdMutableRequestAttributes attributes = new StdMutableRequestAttributes();
		// only check subUser if there can be a relation
		if (action == Action.SAVE || action == Action.DELETE) {
			AttributeProviderUtil
					.createAttribute(DataTypes.DT_BOOLEAN, PREFIX + "isSubUser", reRep.isSubUser(user.getId()))
					.ifPresent(attributes::add);
		}
		AttributeProviderUtil.createAttribute(DataTypes.DT_STRING, ID_ROLE,
				Optional.ofNullable(user.getRole()).map(r -> r.getName())).ifPresent(attributes::add);
		AttributeProviderUtil.createAttribute(DataTypes.DT_INTEGER, ID_COMPANY,
				Optional.ofNullable(user.getCompany()).map(c -> (int) c.getId())).ifPresent(attributes::add);

		// resource as attribute-category
		attributes.setCategory(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
		return attributes;
	}

}
