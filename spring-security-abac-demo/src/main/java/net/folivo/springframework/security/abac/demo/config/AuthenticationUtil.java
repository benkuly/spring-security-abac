package net.folivo.springframework.security.abac.demo.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;
import net.folivo.springframework.security.abac.demo.entities.User;

@Component
public class AuthenticationUtil {

	private final StdUserRepository usRep;

	@Autowired
	public AuthenticationUtil(StdUserRepository usRep) {
		this.usRep = usRep;
	}

	public Optional<User> getCurrentLoggedInUser() {
		return usRep.findByUsernameIgnoreCase(getCurrentLoggedInUsername());
	}

	public static String getCurrentLoggedInUserRole() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.map(a -> a.getAuthority().split("_")[1]).findFirst().orElse("ANONYMOUS");
	}

	public static String getCurrentLoggedInUsername() {
		return getCurrentLoggendInUserDetails().map(d -> d.getUsername()).orElse("anonymous");
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
