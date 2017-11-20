package net.folivo.springframework.security.abac.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.StdPostingRepository;

@RestController
@RequestMapping("/postings")
public class PostingController extends SimpleRestController<Posting, Long> {

	@Autowired
	public PostingController(StdPostingRepository repo) {
		super(repo);
	}

}
