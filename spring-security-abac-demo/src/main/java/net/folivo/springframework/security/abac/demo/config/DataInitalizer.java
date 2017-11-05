package net.folivo.springframework.security.abac.demo.config;

import org.springframework.beans.factory.InitializingBean;

import net.folivo.springframework.security.abac.demo.entities.Company;
import net.folivo.springframework.security.abac.demo.entities.CompanyRepository;
import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;

public class DataInitalizer implements InitializingBean {

	private final UserRepository userRepo;
	private final CompanyRepository companyRepo;

	public static final String PWD = "password";
	public static final String ROLE_ADMIN = "ADMIN";

	public DataInitalizer(UserRepository userRepo, CompanyRepository companyRepo) {
		this.userRepo = userRepo;
		this.companyRepo = companyRepo;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Company c1 = companyRepo.save(new Company("Dr. Hans GmbH", "Wayway 19", "contact@hans-gmbh.de", "0123456789"));
		Company c2 = companyRepo
				.save(new Company("Dr. Franz GmbH", "Upstreet 24", "contact@franz-gmbh.de", "0123456789"));

		User admin = userRepo.save(new User("admin", PWD, ROLE_ADMIN, "Achim", "Admino", null, null));

	}
}
