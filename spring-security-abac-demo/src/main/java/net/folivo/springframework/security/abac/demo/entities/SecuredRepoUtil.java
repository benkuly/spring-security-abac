package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import net.folivo.springframework.security.abac.demo.config.DataInitalizer;

@Component
public class SecuredRepoUtil {

	private final StdUserRepository usRep;

	@Autowired
	public SecuredRepoUtil(StdUserRepository usRep) {
		this.usRep = usRep;
	}

	public User getCurrentLoggedInUser() {
		return usRep.findByUsernameIgnoreCase(getCurrentLoggendInUserDetails().getUsername()).get();
	}

	public UserDetails getCurrentLoggendInUserDetails() {
		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public boolean canSaveUser(User newUser) {
		Optional<User> potentialOldUser = usRep.findById(newUser.getId());
		User subj = getCurrentLoggedInUser();
		if (potentialOldUser.isPresent()) {
			User oldUser = potentialOldUser.get();
			// check readonly attributes
			if (oldUser.getCompany().getId() != newUser.getCompany().getId()
					|| oldUser.getOwner().getId() != newUser.getOwner().getId()
					|| oldUser.getRole().equals(newUser.getRole()))
				return false;
			// if subject is owner and doctor
			if (oldUser.getOwner().getId() == subj.getId() && subj.getRole().equals(DataInitalizer.ROLE_DOCTOR))
				return true;
			// if subject is same user
			if (oldUser.getId() == subj.getId())
				return true;
		}
		if (newUser.getCompany().getId() == subj.getCompany().getId() && newUser.getOwner().getId() == subj.getId())
			return true;
		return false;
	}

	public boolean canDeleteUser(Long id) {
		Optional<User> potentialOldUser = usRep.findById(id);
		User subj = getCurrentLoggedInUser();
		if (potentialOldUser.isPresent()) {
			User oldUser = potentialOldUser.get();
			if (oldUser.getOwner().getId() == subj.getId())
				return true;
		}
		return false;
	}

}
