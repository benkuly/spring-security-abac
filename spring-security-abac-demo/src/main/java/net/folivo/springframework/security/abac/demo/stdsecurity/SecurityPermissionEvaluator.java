package net.folivo.springframework.security.abac.demo.stdsecurity;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import net.folivo.springframework.security.abac.demo.config.AuthenticationUtil;
import net.folivo.springframework.security.abac.demo.config.WebSecurityConfig;
import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.User;

@Profile("stdSecurity")
@Component
public class SecurityPermissionEvaluator implements PermissionEvaluator {

	public final static String SAVE_USER = "SAVE_USER";
	public final static String DELETE_USER = "DELETE_USER";
	public final static String SAVE_POSTING = "SAVE_POSTING";

	private final StdUserRepository usRep;
	private final StdPostingRepository poRep;

	@Autowired
	public SecurityPermissionEvaluator(StdUserRepository usRep, StdPostingRepository poRep) {
		this.usRep = usRep;
		this.poRep = poRep;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		Assert.isTrue(permission instanceof String, "permission must be a String");
		if (targetDomainObject == null)
			return false;
		switch ((String) permission) {
		case SAVE_USER:
			return canSaveUser((User) targetDomainObject);
		case DELETE_USER:
			return canDeleteUser((Optional<User>) targetDomainObject);
		case SAVE_POSTING:
			return canSavePosting((Posting) targetDomainObject);
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
			Assert.isInstanceOf(User.class, newUser);
			Optional<User> potentialOldUser = usRep.findByIdInternal(newUser.getId());
			String role = AuthenticationUtil.getCurrentLoggedInUserRole();
			// handle save
			if (potentialOldUser.isPresent()) {
				User oldUser = potentialOldUser.get();

				switch (role) {
				case WebSecurityConfig.ROLE_ADMIN:
					return true;
				case WebSecurityConfig.ROLE_NORMAL:
					if (oldUser.getRole().equals(newUser.getRole())
							&& oldUser.getUsername().equals(newUser.getUsername())
							&& AuthenticationUtil.getCurrentLoggedInUsername().map(u -> oldUser.getUsername().equals(u))
									.orElse(false))
						return true;
				}
			}
			// handle create
			else {
				switch (role) {
				case WebSecurityConfig.ROLE_ADMIN:
					return true;
				case WebSecurityConfig.ROLE_NORMAL:
				case "ANONYMOUS":
					if (WebSecurityConfig.ROLE_NORMAL.equals(newUser.getRole()) && newUser.getId() == 0)
						return true;
				}
			}
		}
		return false;
	}

	private boolean canDeleteUser(Optional<User> user) {
		if (AuthenticationUtil.getCurrentLoggedInUserRole().equals(WebSecurityConfig.ROLE_ADMIN))
			return true;
		if (user.isPresent())
			return AuthenticationUtil.getCurrentLoggedInUsername().map(u -> u.equals(user.get().getUsername()))
					.orElse(false);
		return false;
	}

	public boolean canSavePosting(Posting newPosting) {
		if (newPosting != null) {
			Assert.isInstanceOf(Posting.class, newPosting);
			Optional<Posting> potentialOldPosting = poRep.findById(newPosting.getId());
			String role = AuthenticationUtil.getCurrentLoggedInUserRole();
			// handle save
			if (potentialOldPosting.isPresent()) {
				Posting oldPosting = potentialOldPosting.get();

				switch (role) {
				case WebSecurityConfig.ROLE_ADMIN:
					return true;
				case WebSecurityConfig.ROLE_NORMAL:
					if (oldPosting.getCreationTime().equals(newPosting.getCreationTime())
							&& oldPosting.getCreator().getUsername().equals(newPosting.getCreator().getUsername())
							&& AuthenticationUtil.getCurrentLoggedInUsername()
									.map(u -> oldPosting.getCreator().getUsername().equals(u)).orElse(false))
						return true;
				}
			}
			// handle create
			else {
				if (AuthenticationUtil.getCurrentLoggedInUsername()
						.map(u -> newPosting.getCreator().getUsername().equals(u)).orElse(false)
						&& newPosting.getId() == 0)
					return true;
			}
		}
		return false;
	}

}
