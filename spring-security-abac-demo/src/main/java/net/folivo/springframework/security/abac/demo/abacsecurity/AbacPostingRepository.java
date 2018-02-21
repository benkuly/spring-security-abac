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
			actionAttributes = { @AttributeMapping(id = "actionId", value = "POSTING_SAVE") }, //
			resourceAttributes = { @AttributeMapping(id = "resource.creator", value = "${#entity.creator.username}"), //
					@AttributeMapping(id = "resource.creationTime", value = "${#entity.creationTime}"), //
					@AttributeMapping(id = "resource.present", value = "${@abacPostingRepository.findByIdInternal(#entity.id).isPresent()}"), //
					@AttributeMapping(id = "resource.old.creationTime", value = "${@abacPostingRepository.findByIdInternal(#entity.id).orElse(null)?.creationTime}"), //
					@AttributeMapping(id = "resource.old.creator", value = "${@abacPostingRepository.findByIdInternal(#entity.id).orElse(null)?.creator?.username}") })
	@Override
	<S extends Posting> S save(@Param("entity") S entity);

	@Override
	Optional<Posting> findById(Long id);

	@AbacPreAuthorize(//
			actionAttributes = { @AttributeMapping(id = "actionId", value = "POSTING_DELETE") }, //
			resourceAttributes = {
					@AttributeMapping(id = "resource.creator", value = "${@abacPostingRepository.findByIdInternal(#id).orElse(null)?.creator?.username}") }) //

	@Override
	void deleteById(@Param("id") Long id);

}
