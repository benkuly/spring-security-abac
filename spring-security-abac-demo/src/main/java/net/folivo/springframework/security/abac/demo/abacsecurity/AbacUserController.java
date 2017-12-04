package net.folivo.springframework.security.abac.demo.abacsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.controller.UserController;

@Profile("abacSecurity")
@RestController
@RequestMapping("/abac/users")
public class AbacUserController extends UserController {

	@Autowired
	public AbacUserController(AbacUserRepository repo) {
		super(repo);
	}

}
