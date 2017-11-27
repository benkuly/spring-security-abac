package net.folivo.springframework.security.abac.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.stdsecurity.AuthenticationUtil;

@RestController
public class TestController {

	@GetMapping("/test")
	public String test(Authentication auth) {
		return AuthenticationUtil.getCurrentLoggedInUsername() + " " + AuthenticationUtil.getCurrentLoggedInUserRole();
	}

}
