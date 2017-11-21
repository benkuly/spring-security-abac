package net.folivo.springframework.security.abac.demo.jackson;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.PropertyWriter;

import net.folivo.springframework.security.abac.demo.config.AuthenticationUtil;
import net.folivo.springframework.security.abac.demo.config.WebSecurityConfig;
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
		if (entity.getUsername().equals(subjUsername) || writer.getName().equals("username")
				|| writer.getName().equals("forename") || writer.getName().equals("id")
				|| AuthenticationUtil.getCurrentLoggedInUserRole().equals(WebSecurityConfig.ROLE_ADMIN))
			return true;
		return false;
	}

}
