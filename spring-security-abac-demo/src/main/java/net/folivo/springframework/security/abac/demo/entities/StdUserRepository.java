package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface StdUserRepository extends PagingAndSortingRepository<User, Long> {

	public Optional<User> findByUsernameIgnoreCase(String username);

	@PreAuthorize("@SecuredRepoUtil.canSaveUser(#entity)")
	@Override
	<S extends User> S save(S entity);

	@Query("SELECT u FROM User u WHERE u.id=?1 AND u.company=(SELECT u.company FROM User u WHERE u.username=?#{principal.username})")
	@Override
	Optional<User> findById(Long id);

	@PreAuthorize("@SecuredRepoUtil.canDeleteUser(#id)")
	@Override
	void deleteById(Long id);
}
