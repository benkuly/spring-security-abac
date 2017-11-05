package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends PagingAndSortingRepository<Company, Long> {

	public Optional<Company> findByName(String name);

	@PreAuthorize("hasRole('ADMIN')")
	@Override
	<S extends Company> S save(S entity);

	@Override
	Iterable<Company> findAll();

	@PreAuthorize("hasRole('ADMIN')")
	@Override
	void delete(Company entity);

}
