package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface StdUserRepository extends CrudRepository<User, Long> {

	public Optional<User> findByUsernameIgnoreCase(String username);

	@PreAuthorize("hasPermission(#entity,'SAVE_USER')")
	@Override
	<S extends User> S save(@Param("entity") S entity);

	@Override
	@PreAuthorize("isAuthenticated()")
	@Query("SELECT u FROM User u WHERE (u.username=?#{principal.username} OR 1=?#{hasRole('ADMIN') ? 1 : 0}) AND u.id=?1")
	Optional<User> findById(Long id);

	@Query("SELECT u FROM User u WHERE u.id=?1")
	Optional<User> findByIdInternal(Long id);

	@PreAuthorize("hasRole('ADMIN')")
	@Override
	void deleteById(Long id);
}
