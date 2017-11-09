package net.folivo.springframework.security.abac.demo.config;

import org.springframework.beans.factory.InitializingBean;

import net.folivo.springframework.security.abac.demo.entities.Company;
import net.folivo.springframework.security.abac.demo.entities.StdCompanyRepository;
import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;

public class DataInitalizer implements InitializingBean {

	private final StdUserRepository userRepo;
	private final StdCompanyRepository companyRepo;

	public static final String PWD = "password";
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_DOCTOR = "DOCTOR";
	public static final String ROLE_PATIENT = "PATIENT";

	public DataInitalizer(StdUserRepository userRepo, StdCompanyRepository companyRepo) {
		this.userRepo = userRepo;
		this.companyRepo = companyRepo;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Company c1 = companyRepo.save(new Company("Dr. Hans GmbH", "Wayway 19", "contact@hans-gmbh.de", "0123456789"));
		Company c2 = companyRepo
				.save(new Company("Dr. Franz GmbH", "Upstreet 24", "contact@franz-gmbh.de", "0123456789"));

		User admin = userRepo.save(new User("admin", PWD, ROLE_ADMIN, "Achim", "Admino", null, null));

		User hans = userRepo.save(new User("hans", PWD, ROLE_DOCTOR, "Dr.", "Hans", null, c1));
		User franz = userRepo.save(new User("franz", PWD, ROLE_DOCTOR, "Dr.", "Franz", null, c2));

		User patientHans = userRepo.save(new User("patientHans", PWD, ROLE_PATIENT, "Patient", "Hans", hans, c1));
		User patientFranz = userRepo.save(new User("patientFranz", PWD, ROLE_PATIENT, "Patient", "Franz", franz, c2));

	}
}
