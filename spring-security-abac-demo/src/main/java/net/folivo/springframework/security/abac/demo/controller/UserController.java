package net.folivo.springframework.security.abac.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;
import net.folivo.springframework.security.abac.demo.entities.User;

@RestController
@RequestMapping("/users")
public class UserController {

	private final StdUserRepository repo;

	@Autowired
	public UserController(StdUserRepository repo) {
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
