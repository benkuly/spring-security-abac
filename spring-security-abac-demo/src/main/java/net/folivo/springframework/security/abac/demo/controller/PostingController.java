package net.folivo.springframework.security.abac.demo.controller;

import java.time.LocalDateTime;
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

import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.PostingResource;
import net.folivo.springframework.security.abac.demo.entities.StdPostingRepository;
import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;

@RestController
@RequestMapping("/postings")
public class PostingController {

	private final StdPostingRepository repo;
	private final StdUserRepository userRepo;

	@Autowired
	public PostingController(StdPostingRepository repo, StdUserRepository userRepo) {
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
