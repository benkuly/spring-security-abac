package net.folivo.springframework.security.abac.demo.abacsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.controller.PostingController;
import net.folivo.springframework.security.abac.demo.stdsecurity.StdPostingRepository;
import net.folivo.springframework.security.abac.demo.stdsecurity.StdUserRepository;

@RestController
@RequestMapping("/abac/postings")
public class AbacPostingController extends PostingController {

	@Autowired
	public AbacPostingController(AbacPostingRepository repo, AbacUserRepository userRepo) {
		super(repo, userRepo);
	}

}
