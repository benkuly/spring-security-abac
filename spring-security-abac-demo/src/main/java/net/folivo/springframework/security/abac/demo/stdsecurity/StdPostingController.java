package net.folivo.springframework.security.abac.demo.stdsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.controller.PostingController;

@RestController
@RequestMapping("/std/postings")
public class StdPostingController extends PostingController {

	@Autowired
	public StdPostingController(StdPostingRepository repo, StdUserRepository userRepo) {
		super(repo, userRepo);
	}

}
