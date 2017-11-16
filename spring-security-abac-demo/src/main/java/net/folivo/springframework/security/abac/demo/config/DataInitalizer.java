package net.folivo.springframework.security.abac.demo.config;

import org.springframework.beans.factory.InitializingBean;

import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;
import net.folivo.springframework.security.abac.demo.entities.User;

public class DataInitalizer implements InitializingBean {

	private final StdUserRepository userRepo;

	public static final String PWD = "password";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_NORMAL = "ROLE_NORMAL";

	public DataInitalizer(StdUserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		userRepo.save(new User("admin", PWD, ROLE_ADMIN, "Achim", "Admino", "mail@mail.mail"));

		userRepo.save(new User("normal", PWD, ROLE_NORMAL, "Norman", "Normalo", "mail@mail.mail"));

	}
}
