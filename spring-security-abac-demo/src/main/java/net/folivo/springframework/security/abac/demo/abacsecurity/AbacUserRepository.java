package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;
import net.folivo.springframework.security.abac.prepost.AbacPreAuthorize;
import net.folivo.springframework.security.abac.prepost.AttributeMapping;

@Profile("abacSecurity")
@Repository
public interface AbacUserRepository extends UserRepository {

	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "actionId", value = "'USER_SAVE'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource.role", value = "#entity.role"), //
					@AttributeMapping(id = "resource.username", value = "#entity.username"), //
					@AttributeMapping(id = "resource.present", value = "@abacUserRepository.findByIdInternal(#entity.id).isPresent()"), })
	@Override
	<S extends User> S save(@Param("entity") S entity);

	// TODO evalutate advice
	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "actionId", value = "'USER_GET'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource.id", value = "#id") })
	@Override
	Optional<User> findById(Long id);

	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "actionId", value = "'USER_DELETE'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource.id", value = "#id") })
	@Override
	void deleteById(Long id);
}
