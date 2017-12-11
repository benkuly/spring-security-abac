package net.folivo.springframework.security.abac.demo.abacsecurity;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.PostingRepository;
import net.folivo.springframework.security.abac.method.AbacPreAuthorize;
import net.folivo.springframework.security.abac.method.AttributeMapping;

@Profile("abacSecurity")
@Repository
public interface AbacPostingRepository extends PostingRepository {

	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "action", value = "'SAVE_POSTING'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource", value = "#entity") })
	@Override
	<S extends Posting> S save(@Param("entity") S entity);

	// TODO evalutate advice
	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "action", value = "'GET_POSTING'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource.id", value = "#id") })
	@Override
	Optional<Posting> findById(Long id);

	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "action", value = "'DELETE_POSTING'") }, //
			resourceAttributes = { @AttributeMapping(id = "resource.id", value = "#id") })
	@Override
	void deleteById(@Param("id") Long id);

}
