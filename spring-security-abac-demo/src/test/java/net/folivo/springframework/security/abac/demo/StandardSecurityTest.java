package net.folivo.springframework.security.abac.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

import net.folivo.springframework.security.abac.demo.config.WebSecurityConfig;
import net.folivo.springframework.security.abac.demo.entities.Posting;
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
		createEntity(userWithRole(WebSecurityConfig.ROLE_NORMAL), "/users", 0);
		userRepoSize(1);
		// try to create ADMIN
		createEntity(userWithRole(WebSecurityConfig.ROLE_ADMIN), "/users", 2);
		userRepoSize(1);
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void createUserAsAdminTest() throws Exception {
		// create NORMAL
		createEntity(userWithRole(WebSecurityConfig.ROLE_NORMAL), "/users", 0);
		userRepoSize(1);
		// create ADMIN
		createEntity(userWithRole(WebSecurityConfig.ROLE_ADMIN), "/users", 0);
		userRepoSize(2);
	}

	@Test
	public void createUserWithId() throws Exception {
		// try to create a user with specific id
		User user = userWithRole(WebSecurityConfig.ROLE_NORMAL);
		ReflectionTestUtils.setField(user, "id", 24L);
		createEntity(user, "/users", 2);
		userRepoSize(0);
	}

	/*-
	 * #############################################
	 * save existing user
	 * #############################################
	 */

	@Test
	public void saveUserAsAnyoneTest() throws Exception {
		// try to change foreign user
		changeExistingUserTest(eMa.persist(userWithRole(WebSecurityConfig.ROLE_NORMAL)), "username", "newUsername", 2);
		changeExistingUserTest(eMa.persist(userWithRole(WebSecurityConfig.ROLE_NORMAL)), "role", "newRole", 2);
	}

	@Test
	@WithMockUser(username = "normalUser", roles = { WebSecurityConfig.ROLE_NORMAL })
	public void saveUserAsNormalTest() throws Exception {
		User normalUser = eMa.persist(userWithUsername("normalUser", WebSecurityConfig.ROLE_NORMAL));
		// try to change own username
		changeExistingUserTest(normalUser, "username", "newUsername", 1);
		// try to change own role
		changeExistingUserTest(normalUser, "role", "newRole", 1);
		// change email
		changeExistingUserTest(normalUser, "email", "newEmail", 0);
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void saveUserAsAdminTest() throws Exception {
		User normalUser = eMa.persist(userWithUsername("normalUser", WebSecurityConfig.ROLE_NORMAL));
		// change username
		changeExistingUserTest(normalUser, "username", "newUsername", 0);
		// change role
		changeExistingUserTest(normalUser, "role", "newRole", 0);
		// change email
		changeExistingUserTest(normalUser, "email", "newEmail", 0);
	}

	// 0 ok, 1 fobidden, 2 unauthorized
	private void changeExistingUserTest(User user, String fieldName, Object value, int allowed) throws Exception {
		eMa.flush();
		eMa.clear();
		long id = user.getId();
		Object original = ReflectionTestUtils.getField(user, fieldName);
		ReflectionTestUtils.setField(user, fieldName, value);
		ResultMatcher result;
		if (allowed == 0)
			result = status().isOk();
		else if (allowed == 1)
			result = status().isForbidden();
		else
			result = status().isUnauthorized();
		mvc.perform(postJson("/users", user)).andExpect(result);
		boolean isSame = ReflectionTestUtils.getField(eMa.find(User.class, id), fieldName).equals(original);
		if (allowed == 0)
			assertTrue("entity wan't changed but expected", !isSame);
		else
			assertTrue("entity was changed but not expected", isSame);
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
		long id = eMa.persist(userWithRole(WebSecurityConfig.ROLE_NORMAL)).getId();
		userRepoSize(1);
		mvc.perform(delete("/users/" + id)).andExpect(status().isUnauthorized());
		userRepoSize(1);
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void deleteUserAsAdminTest() throws Exception {
		// delete a user
		long id = eMa.persist(userWithRole(WebSecurityConfig.ROLE_NORMAL)).getId();
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
		long id = eMa.persist(userWithRole(WebSecurityConfig.ROLE_NORMAL)).getId();
		getUserWithForbiddenAttributes(id, "role", "surname", "email", "password");
	}

	@Test
	@WithMockUser(username = "normalUser", roles = { WebSecurityConfig.ROLE_NORMAL })
	public void getUserAsSameUser() throws Exception {
		long id = eMa.persist(userWithUsername("normalUser", WebSecurityConfig.ROLE_NORMAL)).getId();
		getUserWithForbiddenAttributes(id, "password");
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void getUserAsAdminTest() throws Exception {
		// get a user
		long id = eMa.persist(userWithRole(WebSecurityConfig.ROLE_NORMAL)).getId();
		getUserWithForbiddenAttributes(id, "password");
	}

	private void getUserWithForbiddenAttributes(long id, String... forbiddenAttributes) throws Exception {
		ResultActions result = mvc.perform(get("/users/" + id).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		List<String> forbidden = Arrays.asList(forbiddenAttributes);
		List<String> needed = Arrays.stream(User.class.getDeclaredFields()).map(f -> f.getName())
				.filter(f -> !forbidden.contains(f)).collect(Collectors.toList());

		for (String a : forbidden) {
			result.andExpect(jsonPath("$." + a).doesNotExist());
		}
		for (String a : needed) {
			result.andExpect(jsonPath("$." + a).exists());
		}
	}

	/*-
	 * #############################################
	 * create new posting
	 * #############################################
	 */

	@Test
	public void createPostingAsAnyoneTest() throws Exception {
		// try to create postings
		createEntity(postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)), "/postings", 2);
		postingRepoSize(0);
		createEntity(postingWithCreator(null), "/postings", 2);
		postingRepoSize(0);
	}

	@Test
	@WithMockUser(username = "someUser", roles = { WebSecurityConfig.ROLE_ADMIN })
	public void createPostingAsAdminTest() throws Exception {
		// try create posting with null creator
		createEntity(postingWithCreator(null), "/postings", 1);
		postingRepoSize(0);
		// try create posting with foreign creator
		createEntity(postingWithCreator(eMa.persist(userWithUsername("another"))), "/postings", 1);
		postingRepoSize(0);
		// create posting
		createEntity(postingWithCreator(eMa.persist(userWithUsername("someUser"))), "/postings", 0);
		postingRepoSize(1);
	}

	@Test
	@WithMockUser(username = "someUser", roles = { WebSecurityConfig.ROLE_ADMIN })
	public void createPostingWithId() throws Exception {
		// try to create a posting with specific id
		Posting posting = postingWithCreator(eMa.persist(userWithUsername("someUser")));
		ReflectionTestUtils.setField(posting, "id", 24L);
		createEntity(posting, "/postings", 2);
		userRepoSize(0);
	}

	/*-
	 * #############################################
	 * save existing posting
	 * #############################################
	 */

	/*-
	 * #############################################
	 * delete posting
	 * #############################################
	 */

	/*-
	 * #############################################
	 * get posting
	 * #############################################
	 */

	/*-
	 * #############################################
	 * helping stuff
	 * #############################################
	 */

	// 0 ok, 1 fobidden, 2 unauthorized
	private void createEntity(Object entity, String url, int allowed) throws Exception {
		ResultMatcher httpStatus;
		if (allowed == 0)
			httpStatus = status().isOk();
		else if (allowed == 1)
			httpStatus = status().isForbidden();
		else
			httpStatus = status().isUnauthorized();
		ResultActions result = mvc.perform(postJson(url, entity)).andExpect(httpStatus);
		if (allowed == 0) {
			long id = json.readTree(result.andReturn().getResponse().getContentAsString()).get("id").asLong();
			ReflectionTestUtils.setField(entity, "id", id);
			compareEntities(entity, eMa.find(entity.getClass(), id));
		}
	}

	private MockHttpServletRequestBuilder postJson(String url, Object entity) throws Exception {
		return post(url).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(entity))
				.accept(MediaType.APPLICATION_JSON);
	}

	private User userWithRole(String role) {
		return userWithUsername(UUID.randomUUID().toString(), role);
	}

	private Posting postingWithCreator(User creator) {
		return new Posting(creator, LocalDateTime.now(), "some text");
	}

	private User userWithUsername(String username, String role) {
		return new User(username, "password", role, "forename", "surname", "email");
	}

	private User userWithUsername(String username) {
		return userWithUsername(username, null);
	}

	private void userRepoSize(long expected) {
		assertEquals(new Long(expected),
				eMa.getEntityManager().createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult());
	}

	private void postingRepoSize(long expected) {
		assertEquals(new Long(expected),
				eMa.getEntityManager().createQuery("SELECT COUNT(p) FROM Post p", Long.class).getSingleResult());
	}

	private class PostingResource {

	}

	private void compareEntities(Object one, Object two) {
		Arrays.stream(one.getClass().getDeclaredFields()).map(f -> f.getName())
				.forEach(f -> assertEquals(ReflectionTestUtils.getField(one, f), ReflectionTestUtils.getField(two, f)));
	}
}
