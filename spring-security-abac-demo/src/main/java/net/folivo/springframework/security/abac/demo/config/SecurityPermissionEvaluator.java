package net.folivo.springframework.security.abac.demo.config;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;
import net.folivo.springframework.security.abac.demo.entities.User;

@Component
public class SecurityPermissionEvaluator implements PermissionEvaluator {

	public final static String SAVE_USER = "SAVE_USER";

	private final AuthenticationUtil authn;
	private final StdUserRepository usRep;

	@Autowired
	public SecurityPermissionEvaluator(AuthenticationUtil authn, StdUserRepository usRep) {
		this.authn = authn;
		this.usRep = usRep;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		Assert.isTrue(permission instanceof String, "permission must be a String");
		if (targetDomainObject == null)
			return false;
		switch ((String) permission) {
		case SAVE_USER:
			Assert.isInstanceOf(User.class, targetDomainObject);
			return canSaveUser((User) targetDomainObject);

		}

		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		return false;
	}

	public boolean canSaveUser(User newUser) {
		if (newUser != null) {
			Optional<User> potentialOldUser = usRep.findById(newUser.getId());
			String role = authn.getCurrentLoggedInUserRole();
			// handle save
			if (potentialOldUser.isPresent()) {
				User oldUser = potentialOldUser.get();

				switch (role) {
				case DataInitalizer.ROLE_ADMIN:
					return true;
				case DataInitalizer.ROLE_NORMAL:
					System.out.println("old:" + oldUser.getUsername() + " new:" + newUser.getUsername() + " authn:"
							+ authn.getCurrentLoggedInUsername());
					if (oldUser.getRole().equals(newUser.getRole())
							&& oldUser.getUsername().equals(newUser.getUsername())
							&& oldUser.getUsername().equals(authn.getCurrentLoggedInUsername()))
						return true;
				}
			}
			// handle create
			else {
				switch (role) {
				case DataInitalizer.ROLE_ADMIN:
					return true;
				case DataInitalizer.ROLE_NORMAL:
				case "ROLE_ANONYMOUS":
					if (DataInitalizer.ROLE_NORMAL.equals(newUser.getRole()))
						return true;
				}
			}
		}
		return false;
	}

}
