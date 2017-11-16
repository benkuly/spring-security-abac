package net.folivo.springframework.security.abac.demo;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import net.folivo.springframework.security.abac.demo.config.DataInitalizer;
import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;
import net.folivo.springframework.security.abac.demo.entities.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class StandardSecurityTest {

	@Autowired
	private MockMvc mvc;

	private ObjectMapper json = Jackson2ObjectMapperBuilder.json()
			.filters(new SimpleFilterProvider().setDefaultFilter(new SimpleBeanPropertyFilter() {
			})).build().disable(MapperFeature.USE_ANNOTATIONS);

	@Autowired
	private StdUserRepository usRep;

	@Autowired
	private TestEntityManager eMa;

	@Before
	public void clearDatabase() {
		usRep.deleteAll();
		usRepSize(0);
	}

	/*-
	 * #############################################
	 * create user
	 * #############################################
	 */

	@Test
	public void createUserAsAnyoneTest() throws Exception {
		// create NORMAL
		mvc.perform(postJson("/users", userWithRole(DataInitalizer.ROLE_NORMAL))).andExpect(status().isOk());
		usRepSize(1);
		// try to create ADMIN
		mvc.perform(postJson("/users", userWithRole(DataInitalizer.ROLE_ADMIN))).andExpect(status().isForbidden());
		usRepSize(1);
	}

	@Test
	@WithMockUser(authorities = { DataInitalizer.ROLE_ADMIN })
	public void createUserAsAdminTest() throws Exception {
		// create NORMAL
		mvc.perform(postJson("/users", userWithRole(DataInitalizer.ROLE_NORMAL))).andExpect(status().isOk());
		usRepSize(1);
		// create ADMIN
		mvc.perform(postJson("/users", userWithRole(DataInitalizer.ROLE_ADMIN))).andExpect(status().isOk());
		usRepSize(2);
	}

	/*-
	 * #############################################
	 * save existing user
	 * #############################################
	 */

	@Test
	public void saveUserAsAnyoneTest() throws Exception {
		// try to change foreign user
		changeExistingUserTest(eMa.persist(userWithRole(DataInitalizer.ROLE_NORMAL)), "username", "newUsername", false);
		changeExistingUserTest(eMa.persist(userWithRole(DataInitalizer.ROLE_NORMAL)), "role", "newRole", false);
	}

	@Test
	@WithMockUser(username = "normalUser", authorities = { DataInitalizer.ROLE_NORMAL })
	public void saveUserAsNormalTest() throws Exception {
		User normalUser = eMa.persist(userWithUsername("normalUser", DataInitalizer.ROLE_NORMAL));
		// try to change own username
		changeExistingUserTest(normalUser, "username", "newUsername", false);
		// try to change own role
		changeExistingUserTest(normalUser, "role", "newRole", false);
		// change email
		changeExistingUserTest(normalUser, "email", "newEmail", true);

	}

	private void changeExistingUserTest(User user, String fieldName, Object value, boolean allowed) throws Exception {
		eMa.flush();
		eMa.clear();
		long id = user.getId();
		Object original = ReflectionTestUtils.getField(user, fieldName);
		ReflectionTestUtils.setField(user, fieldName, value);
		ResultMatcher result;
		if (allowed)
			result = status().isOk();
		else
			result = status().isForbidden();
		mvc.perform(postJson("/users", user)).andExpect(result);
		boolean isSame = ReflectionTestUtils.getField(usRep.findById(id).get(), fieldName).equals(original);
		assertTrue(allowed && !isSame || !allowed && isSame);
		ReflectionTestUtils.setField(user, fieldName, original);
	}

	private MockHttpServletRequestBuilder postJson(String url, Object entity) throws Exception {
		return post(url).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(entity))
				.accept(MediaType.APPLICATION_JSON);
	}

	private User userWithRole(String role) {
		return userWithUsername(UUID.randomUUID().toString(), role);
	}

	private User userWithUsername(String username, String role) {
		return new User(username, "password", role, "forename", "surname", "email");
	}

	private void usRepSize(long expected) {
		assertTrue(usRep.count() == expected);
	}

	public static boolean set(Object object, String fieldName, Object fieldValue) throws Exception {
		Class<?> clazz = object.getClass();
		if (clazz != null) {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, fieldValue);
			return true;
		}
		return false;
	}
}
