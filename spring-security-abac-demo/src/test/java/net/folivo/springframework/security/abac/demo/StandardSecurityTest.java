package net.folivo.springframework.security.abac.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StandardSecurityTest {

	@Autowired
	MockMvc mvc;

	@Test
	public void saveUserTest() {
		// only if the entity is your sub-user or yourself AND you don't change company,
		// role or owner
	}

	@Test
	public void deleteUserTest() {
		// only if the entity is your sub-user
	}

	@Test
	public void getUserTest() {
		// show sub-users and yourself with all properties and the others from same
		// company only with
		// username-property
	}

}
