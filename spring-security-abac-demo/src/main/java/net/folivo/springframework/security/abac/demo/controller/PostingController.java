package net.folivo.springframework.security.abac.demo.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.PostingRepository;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;

public class PostingController {

	private final PostingRepository repo;
	private final UserRepository userRepo;

	public PostingController(PostingRepository repo, UserRepository userRepo) {
		this.repo = repo;
		this.userRepo = userRepo;
	}

	@PostMapping
	public ResponseEntity<PostingResource> save(@RequestBody PostingResource entity) {
		if (entity != null) {
			Posting saved = new Posting(userRepo.findByUsernameIgnoreCase(entity.getCreatorUsername()).orElse(null),
					LocalDateTime.now(), entity.getContent());
			return ResponseEntity.ok(new PostingResource(repo.save(saved)));
		}
		return ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		repo.deleteById(id);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostingResource> get(@PathVariable Long id) {
		Optional<Posting> entity = repo.findById(id);
		if (entity.isPresent()) {
			return ResponseEntity.ok().body(new PostingResource(entity.get()));
		}
		return ResponseEntity.notFound().build();
	}

}
