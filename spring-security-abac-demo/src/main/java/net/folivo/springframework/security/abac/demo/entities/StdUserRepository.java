package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import net.folivo.springframework.security.abac.demo.config.SecurityPermissionEvaluator;

@Repository
public interface StdUserRepository extends PagingAndSortingRepository<User, Long> {

	public Optional<User> findByUsernameIgnoreCase(String username);

	@PreAuthorize("hasPermission(#entity,'" + SecurityPermissionEvaluator.SAVE_USER + "')")
	@Override
	<S extends User> S save(@Param("entity") S entity);

	// @Query("SELECT u FROM User u WHERE u.id=?1 AND u.company=(SELECT t.company
	// FROM User t WHERE t.username=?#{principal.username})")
	@Override
	Optional<User> findById(Long id);

	// @PreAuthorize("@securedRepoUtil.canDeleteUser(#id)")
	@Override
	void deleteById(Long id);
}
