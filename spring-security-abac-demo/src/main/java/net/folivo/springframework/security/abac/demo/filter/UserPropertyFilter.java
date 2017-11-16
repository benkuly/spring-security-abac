package net.folivo.springframework.security.abac.demo.filter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.PropertyWriter;

import net.folivo.springframework.security.abac.demo.config.AuthenticationUtil;
import net.folivo.springframework.security.abac.demo.entities.User;

@Component
public class UserPropertyFilter implements PropertyFilter {

	private final AuthenticationUtil util;

	public UserPropertyFilter(AuthenticationUtil util) {
		this.util = util;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean serialize(Object pojo, PropertyWriter writer) {
		User entity = (User) pojo;
		String subjUsername = util.getCurrentLoggedInUsername();
		if (entity.getUsername() == subjUsername || writer.getName().equals("username")
				|| writer.getName().equals("forename"))
			return true;
		return false;
	}

}
