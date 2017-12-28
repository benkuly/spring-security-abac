package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PostingRepository extends CrudRepository<Posting, Long> {

	@Query("SELECT p FROM Posting p WHERE p.id=?1")
	Optional<Posting> findByIdInternal(Long id);

}
