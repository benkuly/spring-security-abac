package net.folivo.springframework.security.abac.demo.config;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.folivo.springframework.security.abac.demo.entities.User;

@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

	private final EntityManager eMa;

	public static final String PWD = "password";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_NORMAL = "ROLE_NORMAL";

	@Autowired
	public DataInitializer(EntityManager eMa) {
		this.eMa = eMa;
	}

	@Override
	public void run(String... args) throws Exception {
		eMa.persist(new User("admin", PWD, ROLE_ADMIN, "Achim", "Admino", "mail@mail.mail"));

		eMa.persist(new User("normal", PWD, ROLE_NORMAL, "Norman", "Normalo", "mail@mail.mail"));

		System.out.println("\r\nCreated demo enitites\r\n");

	}
}
