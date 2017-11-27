package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;
import net.folivo.springframework.security.abac.prepost.AbacPreAuthorize;
import net.folivo.springframework.security.abac.prepost.AttributeMapping;

@Repository
public interface AbacUserRepository extends UserRepository {

	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "action", value = "'SAVE_USER'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource", value = "#entity") })
	@Override
	<S extends User> S save(@Param("entity") S entity);

	// TODO advice auswerten
	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "action", value = "'GET_USER'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource.id", value = "#id") })
	@Override
	Optional<User> findById(Long id);

	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "action", value = "'DELETE_USER'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource.id", value = "#id") })
	@Override
	void deleteById(Long id);
}
