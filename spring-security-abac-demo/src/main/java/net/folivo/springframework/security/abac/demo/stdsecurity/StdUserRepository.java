package net.folivo.springframework.security.abac.demo.stdsecurity;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;

@Repository
public interface StdUserRepository extends UserRepository {

	@PreAuthorize("hasPermission(#entity,'SAVE_USER')")
	@Override
	<S extends User> S save(@Param("entity") S entity);

	@Override
	@PreAuthorize("isAuthenticated()")
	@Query("SELECT u FROM User u WHERE (u.username=?#{principal.username} OR 1=?#{hasRole('ADMIN') ? 1 : 0}) AND u.id=?1")
	Optional<User> findById(Long id);

	@PreAuthorize("hasRole('ADMIN')")
	@Override
	void deleteById(Long id);
}
