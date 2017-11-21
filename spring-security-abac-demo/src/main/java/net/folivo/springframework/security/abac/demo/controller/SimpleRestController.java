package net.folivo.springframework.security.abac.demo.controller;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class SimpleRestController<T, ID> {

	private final CrudRepository<T, ID> repo;

	public SimpleRestController(CrudRepository<T, ID> repo) {
		this.repo = repo;
	}

	@PostMapping
	public ResponseEntity<T> save(@RequestBody T entity) {
		if (entity != null) {
			return ResponseEntity.ok(repo.save(entity));
		}
		return ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable ID id) {
		repo.deleteById(id);
	}

	@GetMapping("/{id}")
	public ResponseEntity<T> get(@PathVariable ID id) {
		Optional<T> entity = repo.findById(id);
		if (entity.isPresent()) {
			return ResponseEntity.ok().body(entity.get());
		}
		return ResponseEntity.notFound().build();
	}

}
