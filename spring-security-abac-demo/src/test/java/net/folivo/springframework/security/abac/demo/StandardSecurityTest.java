package net.folivo.springframework.security.abac.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import net.folivo.springframework.security.abac.demo.config.DataInitializer;
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
	private TestEntityManager eMa;

	@Before
	public void clearDatabase() {
		eMa.getEntityManager().createQuery("DELETE FROM User").executeUpdate();
		userRepoSize(0);
	}

	/*-
	 * #############################################
	 * create new user
	 * #############################################
	 */

	@Test
	public void createUserAsAnyoneTest() throws Exception {
		// create NORMAL
		mvc.perform(postJson("/users", userWithRole(DataInitializer.ROLE_NORMAL))).andExpect(status().isOk());
		userRepoSize(1);
		// try to create ADMIN
		mvc.perform(postJson("/users", userWithRole(DataInitializer.ROLE_ADMIN))).andExpect(status().isForbidden());
		userRepoSize(1);
	}

	@Test
	@WithMockUser(authorities = { DataInitializer.ROLE_ADMIN })
	public void createUserAsAdminTest() throws Exception {
		// create NORMAL
		mvc.perform(postJson("/users", userWithRole(DataInitializer.ROLE_NORMAL))).andExpect(status().isOk());
		userRepoSize(1);
		// create ADMIN
		mvc.perform(postJson("/users", userWithRole(DataInitializer.ROLE_ADMIN))).andExpect(status().isOk());
		userRepoSize(2);
	}

	/*-
	 * #############################################
	 * save existing user
	 * #############################################
	 */

	@Test
	public void saveUserAsAnyoneTest() throws Exception {
		// try to change foreign user
		changeExistingUserTest(eMa.persist(userWithRole(DataInitializer.ROLE_NORMAL)), "username", "newUsername",
				false);
		changeExistingUserTest(eMa.persist(userWithRole(DataInitializer.ROLE_NORMAL)), "role", "newRole", false);
	}

	@Test
	@WithMockUser(username = "normalUser", authorities = { DataInitializer.ROLE_NORMAL })
	public void saveUserAsNormalTest() throws Exception {
		User normalUser = eMa.persist(userWithUsername("normalUser", DataInitializer.ROLE_NORMAL));
		// try to change own username
		changeExistingUserTest(normalUser, "username", "newUsername", false);
		// try to change own role
		changeExistingUserTest(normalUser, "role", "newRole", false);
		// change email
		changeExistingUserTest(normalUser, "email", "newEmail", true);
	}

	@Test
	@WithMockUser(authorities = { DataInitializer.ROLE_ADMIN })
	public void saveUserAsAdminTest() throws Exception {
		User normalUser = eMa.persist(userWithUsername("normalUser", DataInitializer.ROLE_NORMAL));
		// change username
		changeExistingUserTest(normalUser, "username", "newUsername", true);
		// change role
		changeExistingUserTest(normalUser, "role", "newRole", true);
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
		boolean isSame = ReflectionTestUtils.getField(eMa.find(User.class, id), fieldName).equals(original);
		assertTrue(allowed && !isSame || !allowed && isSame);
		ReflectionTestUtils.setField(user, fieldName, original);
	}

	/*-
	 * #############################################
	 * delete existing user
	 * #############################################
	 */

	@Test
	public void deleteUserAsAnyoneTest() throws Exception {
		// try to delete a user
		long id = eMa.persist(userWithRole(DataInitializer.ROLE_NORMAL)).getId();
		userRepoSize(1);
		mvc.perform(delete("/users/" + id)).andExpect(status().isForbidden());
		userRepoSize(1);
	}

	@Test
	@WithMockUser(authorities = { DataInitializer.ROLE_ADMIN })
	public void deleteUserAsAdminTest() throws Exception {
		// delete a user
		long id = eMa.persist(userWithRole(DataInitializer.ROLE_NORMAL)).getId();
		userRepoSize(1);
		mvc.perform(delete("/users/" + id)).andExpect(status().isOk());
		userRepoSize(0);
	}

	/*-
	 * #############################################
	 * get user
	 * #############################################
	 */

	@Test
	public void getUserAsAnyoneTest() throws Exception {
		// get a user
		long id = eMa.persist(userWithRole(DataInitializer.ROLE_NORMAL)).getId();
		getUserWithAttributes(id, "role", "forename", "email", "password");
	}

	@Test
	@WithMockUser(authorities = { DataInitializer.ROLE_ADMIN })
	public void getUserAsAdminTest() throws Exception {
		// get a user
		long id = eMa.persist(userWithRole(DataInitializer.ROLE_NORMAL)).getId();
		getUserWithAttributes(id);
	}

	private void getUserWithAttributes(long id, String... forbiddenAttributes) throws Exception {
		ResultActions result = mvc.perform(get("/users/" + id).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		List<String> needed = new ArrayList<>();
		List<String> forbidden = Arrays.asList(forbiddenAttributes);
		for (Field field : User.class.getFields()) {
			if (!forbidden.contains(field.getName()))
				needed.add(field.getName());
		}
		for (String a : forbidden) {
			result.andExpect(jsonPath("$." + a).doesNotExist());
		}
		for (String a : needed) {
			System.out.println(a);
			result.andExpect(jsonPath("$." + a).exists());
		}
	}

	/*-
	 * #############################################
	 * create new post
	 * #############################################
	 */

	/*-
	 * #############################################
	 * save existing post
	 * #############################################
	 */

	/*-
	 * #############################################
	 * delete post
	 * #############################################
	 */

	/*-
	 * #############################################
	 * get post
	 * #############################################
	 */

	/*-
	 * #############################################
	 * helping stuff
	 * #############################################
	 */

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

	private void userRepoSize(long expected) {
		assertEquals(new Long(expected),
				eMa.getEntityManager().createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult());
	}

	private void postRepoSize(long expected) {
		assertEquals(new Long(expected),
				eMa.getEntityManager().createQuery("SELECT COUNT(p) FROM Post p", Long.class).getSingleResult());
	}
}
