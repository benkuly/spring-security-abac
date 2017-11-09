package net.folivo.springframework.security.abac.demo.entities;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StdCompanyRepository extends PagingAndSortingRepository<Company, Long> {

}
