package net.folivo.springframework.security.abac.demo.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PostingRepository extends CrudRepository<Posting, Long> {

}
