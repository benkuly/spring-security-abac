package net.folivo.springframework.security.abac.demo.stdsecurity;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.PostingRepository;

@Profile("stdSecurity")
@Repository
public interface StdPostingRepository extends PostingRepository {

	@PreAuthorize("hasPermission(#entity,'SAVE_POSTING')")
	@Override
	<S extends Posting> S save(@Param("entity") S entity);

	@Override
	Optional<Posting> findById(Long id);

	@PreAuthorize("hasRole('ADMIN') || isAuthenticated() && @stdPostingRepository.findById(#id).orElse(null)?.creator.username==principal?.username")
	@Override
	void deleteById(@Param("id") Long id);

}
