package net.folivo.springframework.security.abac.demo.config;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;

@Service
// @Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepo;

	@Autowired
	public CustomUserDetailsService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> domainUser = userRepo.findByUsernameIgnoreCase(username);
		if (!domainUser.isPresent()) {
			throw new UsernameNotFoundException("wrong username: " + username);
		}

		User user = domainUser.get();

		Set<GrantedAuthority> gas = new HashSet<>();
		gas.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), gas);
	}
}