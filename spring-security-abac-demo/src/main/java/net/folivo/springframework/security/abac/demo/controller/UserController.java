package net.folivo.springframework.security.abac.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;

public class UserController {

	private final UserRepository repo;

	@Autowired
	public UserController(UserRepository repo) {
		this.repo = repo;
	}

	@PostMapping
	public ResponseEntity<User> save(@RequestBody User entity) {
		if (entity != null) {
			return ResponseEntity.ok(repo.save(entity));
		}
		return ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		repo.deleteById(id);
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> get(@PathVariable Long id) {
		Optional<User> entity = repo.findById(id);
		if (entity.isPresent()) {
			return ResponseEntity.ok().body(entity.get());
		}
		return ResponseEntity.notFound().build();
	}

}
