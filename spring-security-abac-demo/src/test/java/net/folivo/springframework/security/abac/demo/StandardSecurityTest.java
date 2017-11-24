package net.folivo.springframework.security.abac.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import net.folivo.springframework.security.abac.demo.config.WebSecurityConfig;
import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.StdPostingRepository;
import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;
import net.folivo.springframework.security.abac.demo.entities.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
public class StandardSecurityTest {

	@Autowired
	private TestEntityManager eMa;

	@Autowired
	private StdUserRepository userRepo;

	@Autowired
	private StdPostingRepository postingRepo;

	private RepoSecurityTestHelper<User> userTest;

	private RepoSecurityTestHelper<Posting> postingTest;

	@Before
	public void createTestHelperAndClearDatabase() {
		if (userTest == null)
			userTest = new RepoSecurityTestHelper<>(User.class, userRepo, "id", eMa);
		if (postingTest == null)
			postingTest = new RepoSecurityTestHelper<>(Posting.class, postingRepo, "id", eMa);

		userTest.clearRepository();
		postingTest.clearRepository();
		assertEquals(Long.valueOf(0), userTest.getRepoSize());
		assertEquals(Long.valueOf(0), postingTest.getRepoSize());
	}

	/*-
	 * #############################################
	 * testing save-method-security
	 * #############################################
	 */

	@Test
	@WithAnonymousUser
	public void createUserAsAnyoneTest() throws Exception {
		// create NORMAL
		userTest.testEntitySave(true, userWithRole(WebSecurityConfig.ROLE_NORMAL));
		// try to create ADMIN
		userTest.testEntitySave(false, userWithRole(WebSecurityConfig.ROLE_ADMIN));
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void createUserAsAdminTest() throws Exception {
		// create NORMAL
		userTest.testEntitySave(true, userWithRole(WebSecurityConfig.ROLE_NORMAL));
		// create ADMIN
		userTest.testEntitySave(true, userWithRole(WebSecurityConfig.ROLE_ADMIN));
	}

	@Test
	@WithAnonymousUser
	public void createUserWithId() throws Exception {
		// try to create a user with specific id
		User user = userWithRole(WebSecurityConfig.ROLE_NORMAL);
		ReflectionTestUtils.setField(user, "id", 24L);
		userTest.testEntitySave(false, user);
	}

	@Test
	@WithAnonymousUser
	public void saveUserAsAnyoneTest() throws Exception {
		// try to change foreign user
		userTest.testEntityChange(false, eMa.persistAndFlush(userWithRole(WebSecurityConfig.ROLE_NORMAL)), "username",
				"newUsername");
		userTest.testEntityChange(false, eMa.persistAndFlush(userWithRole(WebSecurityConfig.ROLE_NORMAL)), "role",
				"newRole");
	}

	@Test
	@WithMockUser(username = "someUser", roles = { WebSecurityConfig.ROLE_NORMAL })
	public void saveUserAsNormalTest() throws Exception {
		User someUser = eMa.persistAndFlush(userWithUsername("someUser", WebSecurityConfig.ROLE_NORMAL));
		eMa.detach(someUser);
		// try to change own username
		userTest.testEntityChange(false, someUser, "username", "newUsername");
		// try to change own role
		userTest.testEntityChange(false, someUser, "role", "newRole");
		// change email
		userTest.testEntityChange(true, someUser, "email", "newEmail");
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void saveUserAsAdminTest() throws Exception {
		User someUser = eMa.persistAndFlush(userWithUsername("normalUser", WebSecurityConfig.ROLE_NORMAL));
		eMa.detach(someUser);
		// change username
		userTest.testEntityChange(true, someUser, "username", "newUsername");
		// change role
		userTest.testEntityChange(true, someUser, "role", "newRole");
		// change email
		userTest.testEntityChange(true, someUser, "email", "newEmail");
	}

	/*-
	 * #############################################
	 * delete existing user
	 * #############################################
	 */

	@Test
	@WithAnonymousUser
	public void deleteUserAsAnyoneTest() throws Exception {
		// try to delete a user
		userTest.testEntityDelete(false, userWithRole(WebSecurityConfig.ROLE_NORMAL));
		userTest.testEntityDelete(false, userWithRole(WebSecurityConfig.ROLE_ADMIN));
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void deleteUserAsAdminTest() throws Exception {
		// delete a user
		userTest.testEntityDelete(true, userWithRole(WebSecurityConfig.ROLE_NORMAL));
		userTest.testEntityDelete(true, userWithRole(WebSecurityConfig.ROLE_ADMIN));
	}

	/*-
	 * #############################################
	 * get user
	 * #############################################
	 */

	@Test
	@WithAnonymousUser
	public void getUserAsAnyoneTest() throws Exception {
		// try get a user
		userTest.testEntityGet(false, userWithRole(WebSecurityConfig.ROLE_NORMAL));
		userTest.testEntityGet(false, userWithRole(WebSecurityConfig.ROLE_ADMIN));
	}

	@Test
	@WithMockUser(username = "normalUser", roles = { WebSecurityConfig.ROLE_NORMAL })
	public void getUserAsSameUser() throws Exception {
		// try get a user
		userTest.testEntityGet(false, userWithRole(WebSecurityConfig.ROLE_NORMAL));
		userTest.testEntityGet(false, userWithRole(WebSecurityConfig.ROLE_ADMIN));
		// try get same user
		userTest.testEntityGet(true, userWithUsername("normalUser", WebSecurityConfig.ROLE_NORMAL));
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void getUserAsAdminTest() throws Exception {
		// get a user
		userTest.testEntityGet(true, userWithRole(WebSecurityConfig.ROLE_NORMAL));
		userTest.testEntityGet(true, userWithRole(WebSecurityConfig.ROLE_ADMIN));
	}

	/*-
	 * #############################################
	 * save posting
	 * #############################################
	 */

	@Test
	@WithAnonymousUser
	public void createPostingAsAnyoneTest() throws Exception {
		// try to create postings
		postingTest.testEntitySave(false, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
	}

	@Test
	@WithMockUser(username = "someUser", roles = { WebSecurityConfig.ROLE_ADMIN })
	public void createPostingAsAdminTest() throws Exception {
		// try create posting with foreign creator
		postingTest.testEntitySave(false, postingWithCreator(eMa.persistAndFlush(userWithUsername("another"))));
		// create posting
		postingTest.testEntitySave(true, postingWithCreator(eMa.persistAndFlush(userWithUsername("someUser"))));
	}

	@Test
	@WithMockUser(username = "someUser", roles = { WebSecurityConfig.ROLE_ADMIN })
	public void createPostingWithId() throws Exception {
		// try to create a posting with specific id
		Posting posting = postingWithCreator(eMa.persistAndFlush(userWithUsername("someUser")));
		ReflectionTestUtils.setField(posting, "id", 24L);
		postingTest.testEntitySave(false, posting);
	}

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

	protected class RepoSecurityTestHelper<T> {

		private final TestEntityManager eMa;
		private final Object repo;
		private final String idFieldName;
		private final Class<T> clazzT;

		public RepoSecurityTestHelper(Class<T> clazzT, Object repo, String idFieldName, TestEntityManager eMa) {
			this.eMa = eMa;
			this.clazzT = clazzT;
			this.repo = repo;
			this.idFieldName = idFieldName;
		}

		public void testEntityChange(boolean allowed, T original, String fieldName, Object value) throws Exception {
			Object originalValue = ReflectionTestUtils.getField(original, fieldName);
			ReflectionTestUtils.setField(original, fieldName, value);
			testEntitySave(allowed, original);
			if (!allowed)
				ReflectionTestUtils.setField(original, fieldName, originalValue);
		}

		public void testEntitySave(boolean allowed, T original) throws Exception {
			Object originalId = ReflectionTestUtils.getField(original, idFieldName);
			boolean dbIncreases = allowed
					? original != null && (originalId == null || eMa.find(User.class, originalId) == null)
					: false;
			Long expectedRepoSize = getRepoSize() + (dbIncreases ? 1 : 0);
			T saved = testMethodSecurity(allowed, repo, "save", original);
			if (allowed)
				compareEntities(original, saved);
			assertEquals(expectedRepoSize, getRepoSize());
		}

		public void testEntityDelete(boolean allowed, T original) throws Exception {
			Object originalId = ReflectionTestUtils.getField(eMa.persistAndFlush(original), idFieldName);
			Long expectedRepoSize = getRepoSize() - (allowed ? 1 : 0);
			testMethodSecurity(allowed, userRepo, "deleteById", originalId);
			assertEquals(expectedRepoSize, getRepoSize());
		}

		public void testEntityGet(boolean allowed, T original) throws Exception {
			eMa.persistAndFlush(original);
			boolean methodAllowed = SecurityContextHolder.getContext().getAuthentication().getPrincipal()
					.equals("anonymous") ? allowed : true;
			Optional<T> found = testMethodSecurity(methodAllowed, userRepo, "findById",
					ReflectionTestUtils.getField(eMa.persistAndFlush(original), idFieldName));
			if (allowed) {
				assertTrue(found.isPresent());
				compareEntities(original, found.get());
			} else
				assertTrue(found == null || !found.isPresent());

		}

		public <V> V testMethodSecurity(boolean allowed, Object target, String method, Object... parameter)
				throws Exception {
			V result = null;
			try {
				result = ReflectionTestUtils.invokeMethod(target, method, parameter);
				if (!allowed)
					fail("AccessDeniedException not thrown.");
			} catch (AccessDeniedException e) {
				if (allowed)
					fail("AccessDeniedException was thrown.");
			}
			return result;
		}

		public Long getRepoSize() {
			return eMa.getEntityManager()
					.createQuery("SELECT COUNT(t) FROM " + clazzT.getSimpleName() + " t", Long.class).getSingleResult();
		}

		public void clearRepository() {
			eMa.getEntityManager().createQuery("DELETE FROM " + clazzT.getSimpleName()).executeUpdate();
			eMa.flush();
		}

		public void compareEntities(Object one, Object two) {
			if (one == two || one.equals(two))
				return;
			Arrays.stream(one.getClass().getDeclaredFields()).map(f -> f.getName()).forEach(
					f -> assertEquals(ReflectionTestUtils.getField(one, f), ReflectionTestUtils.getField(two, f)));
		}

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
}
