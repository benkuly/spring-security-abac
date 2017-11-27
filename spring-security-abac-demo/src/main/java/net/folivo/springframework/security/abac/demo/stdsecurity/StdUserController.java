package net.folivo.springframework.security.abac.demo.stdsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.controller.UserController;

@RestController
@RequestMapping("/std/users")
public class StdUserController extends UserController {

	@Autowired
	public StdUserController(StdUserRepository repo) {
		super(repo);
	}

}
