package net.folivo.springframework.security.abac.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;

@RestController("/user")
public class UserController extends SimpleRestController<User, Long> {

	@Autowired
	public UserController(UserRepository repo) {
		super(repo);
	}

}
