package net.folivo.springframework.security.abac.demo.filter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.PropertyWriter;

import net.folivo.springframework.security.abac.demo.entities.SecuredRepoUtil;
import net.folivo.springframework.security.abac.demo.entities.User;

@Component
public class UserPropertyFilter implements PropertyFilter {

	private final SecuredRepoUtil util;

	public UserPropertyFilter(SecuredRepoUtil util) {
		this.util = util;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean serialize(Object pojo, PropertyWriter writer) {
		User entity = (User) pojo;
		User subj = util.getCurrentLoggedInUser();
		if (entity.getOwner().getId() == subj.getId() || writer.getName().equals("username"))
			return true;
		return false;
	}

}
