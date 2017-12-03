package net.folivo.springframework.security.abac.demo.stdsecurity;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import net.folivo.springframework.security.abac.demo.entities.User;

@Component
public class AuthenticationUtil {

	private final StdUserRepository usRep;

	@Autowired
	public AuthenticationUtil(StdUserRepository usRep) {
		this.usRep = usRep;
	}

	public Optional<User> getCurrentLoggedInUser() {
		return getCurrentLoggedInUsername().map(n -> usRep.findByUsernameIgnoreCase(n)).orElse(Optional.empty());
	}

	public static String getCurrentLoggedInUserRole() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.map(a -> a.getAuthority()).map(a -> a.split("_")).filter(a -> a.length > 1).map(a -> a[1]).findFirst()
				.orElse("ANONYMOUS");
	}

	public static Optional<String> getCurrentLoggedInUsername() {
		return getCurrentLoggendInUserDetails().map(d -> Optional.of(d.getUsername())).orElse(Optional.empty());
	}

	public static Optional<UserDetails> getCurrentLoggendInUserDetails() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof UserDetails)
				return Optional.of((UserDetails) principal);
		}
		return Optional.empty();
	}

}
