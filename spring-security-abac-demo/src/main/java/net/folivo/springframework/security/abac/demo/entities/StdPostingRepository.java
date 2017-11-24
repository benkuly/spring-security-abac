package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface StdPostingRepository extends CrudRepository<Posting, Long> {

	@PreAuthorize("hasPermission(#entity,'SAVE_POSTING')")
	@Override
	<S extends Posting> S save(@Param("entity") S entity);

	@Override
	Optional<Posting> findById(Long id);

	@PreAuthorize("hasRole('ADMIN') || isAuthenticated() && @stdPostingRepository.findById(#id).orElse(null)?.creator.username==principal?.username")
	@Override
	void deleteById(@Param("id") Long id);

}
