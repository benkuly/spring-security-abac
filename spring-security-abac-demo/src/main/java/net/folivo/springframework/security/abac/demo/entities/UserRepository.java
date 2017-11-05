package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	public Optional<User> findByUsernameIgnoreCase(String username);

	@PreAuthorize("#entity.owner.username == principal.username && hasRole('DOCTOR')")
	@Override
	<S extends User> S save(S entity);

	@Query("SELECT u FROM User u WHERE u.owner.username=?#{principal.id}")
	@Override
	Iterable<User> findAll();

	@PreAuthorize("#entity.owner.username == principal.username && hasRole('DOCTOR')")
	@Override
	void delete(User entity);
}
