package net.folivo.springframework.security.abac.demo.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StdPostRepository extends CrudRepository<Posting, Long> {

}
