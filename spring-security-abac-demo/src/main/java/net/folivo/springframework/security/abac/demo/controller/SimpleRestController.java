package net.folivo.springframework.security.abac.demo.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class SimpleRestController<T extends Identifiable<ID>, ID extends Serializable> {

	private final CrudRepository<T, ID> repo;

	public SimpleRestController(CrudRepository<T, ID> repo) {
		this.repo = repo;
	}

	@PostMapping
	public ResponseEntity<Resource<T>> save(@RequestBody T entity) {
		if (entity != null) {
			T savedEntity = repo.save(entity);
			Link link = linkTo(this.getClass()).slash(savedEntity).withSelfRel();
			Resource<T> r = new Resource<>(savedEntity, link);
			return ResponseEntity.ok(r);
		}
		return ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable ID id) {
		repo.deleteById(id);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Resource<T>> get(@PathVariable ID id) {
		Optional<T> entity = repo.findById(id);
		if (entity.isPresent()) {
			Link link = linkTo(this.getClass()).slash(entity.get()).withSelfRel();
			Resource<T> r = new Resource<>(entity.get(), link);
			return ResponseEntity.ok().body(r);
		}
		return ResponseEntity.notFound().build();
	}

}
