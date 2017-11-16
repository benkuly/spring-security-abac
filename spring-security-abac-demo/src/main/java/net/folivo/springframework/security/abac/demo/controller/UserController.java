package net.folivo.springframework.security.abac.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;
import net.folivo.springframework.security.abac.demo.entities.User;

@RestController
@RequestMapping("/users")
public class UserController extends SimpleRestController<User, Long> {

	@Autowired
	public UserController(StdUserRepository repo) {
		super(repo);
	}

}
