package net.folivo.springframework.security.abac.demo.filter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.PropertyWriter;

import net.folivo.springframework.security.abac.demo.config.AuthenticationUtil;
import net.folivo.springframework.security.abac.demo.config.DataInitializer;
import net.folivo.springframework.security.abac.demo.entities.User;

@Component
public class UserPropertyFilter implements PropertyFilter {

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean serialize(Object pojo, PropertyWriter writer) {
		User entity = (User) pojo;
		String subjUsername = AuthenticationUtil.getCurrentLoggedInUsername();
		if (entity.getUsername() == subjUsername || writer.getName().equals("username")
				|| writer.getName().equals("forename")
				|| AuthenticationUtil.getCurrentLoggedInUserRole().equals(DataInitializer.ROLE_ADMIN))
			return true;
		return false;
	}

}
