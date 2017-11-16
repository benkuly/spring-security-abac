package net.folivo.springframework.security.abac.demo.entities;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StdPostRepository extends PagingAndSortingRepository<Post, Long> {

}
