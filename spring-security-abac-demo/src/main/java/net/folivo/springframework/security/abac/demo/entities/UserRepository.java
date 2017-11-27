package net.folivo.springframework.security.abac.demo.entities;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserRepository extends CrudRepository<User, Long> {

	public Optional<User> findByUsernameIgnoreCase(String username);

	@Query("SELECT u FROM User u WHERE u.id=?1")
	Optional<User> findByIdInternal(Long id);

}
