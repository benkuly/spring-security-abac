package net.folivo.springframework.security.abac.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import net.folivo.springframework.security.abac.demo.entities.Company;
import net.folivo.springframework.security.abac.demo.entities.CompanyRepository;

@RestController("/company")
public class CompanyController extends SimpleRestController<Company, Long> {

	@Autowired
	public CompanyController(CompanyRepository repo) {
		super(repo);
	}

}
