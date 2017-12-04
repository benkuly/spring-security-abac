package net.folivo.springframework.security.abac.demo;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import net.folivo.springframework.security.abac.demo.config.WebSecurityConfig;
import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.PostingRepository;
import net.folivo.springframework.security.abac.demo.entities.User;
import net.folivo.springframework.security.abac.demo.entities.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
public abstract class SimpleSecurityTest {

	@Autowired
	private TestEntityManager eMa;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PostingRepository postingRepo;

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
	 * user save
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
		User someUser = eMa.persistAndFlush(userWithUsernameRole("someUser", WebSecurityConfig.ROLE_NORMAL));
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
		User someUser = eMa.persistAndFlush(userWithUsernameRole("normalUser", WebSecurityConfig.ROLE_NORMAL));
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
	 * user delete
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
	 * user get
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
		userTest.testEntityGet(true, userWithUsernameRole("normalUser", WebSecurityConfig.ROLE_NORMAL));
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
	 * posting save
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
		postingTest.testEntitySave(false, postingWithCreator(userWithUsername("another")));
		// create posting
		postingTest.testEntitySave(true, postingWithCreator(userWithUsername("someUser")));
	}

	@Test
	@WithMockUser(username = "someUser", roles = { WebSecurityConfig.ROLE_ADMIN })
	public void createPostingWithId() throws Exception {
		// try to create a posting with specific id
		Posting posting = postingWithCreator(userWithUsername("someUser"));
		ReflectionTestUtils.setField(posting, "id", 24L);
		postingTest.testEntitySave(false, posting);
	}

	@Test
	@WithAnonymousUser
	public void savePostingAsAnyoneTest() throws Exception {
		Posting somePosting = eMa.persistAndFlush(postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
		eMa.detach(somePosting);

		postingTest.testEntityChange(false, somePosting, "content", "newContent");
		postingTest.testEntityChange(false, somePosting, "creationTime", LocalDateTime.now().minusDays(24));
		postingTest.testEntityChange(false, somePosting, "creator",
				eMa.persistAndFlush(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
	}

	@Test
	@WithMockUser(username = "someUser", roles = { WebSecurityConfig.ROLE_NORMAL })
	public void savePostingAsNormalTest() throws Exception {
		Posting somePosting = eMa.persistAndFlush(postingWithCreator(userWithUsername("someUser")));
		eMa.detach(somePosting);

		postingTest.testEntityChange(true, somePosting, "content", "newContent");
		postingTest.testEntityChange(false, somePosting, "creationTime", LocalDateTime.now().minusDays(24));
		postingTest.testEntityChange(false, somePosting, "creator",
				eMa.persistAndFlush(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void savePostingAsAdminTest() throws Exception {
		Posting somePosting = eMa.persistAndFlush(postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
		eMa.detach(somePosting);

		postingTest.testEntityChange(true, somePosting, "content", "newContent");
		postingTest.testEntityChange(true, somePosting, "creationTime", LocalDateTime.now().minusDays(24));
		postingTest.testEntityChange(true, somePosting, "creator",
				eMa.persistAndFlush(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
	}

	/*-
	 * #############################################
	 * posting delete
	 * #############################################
	 */

	@Test
	@WithAnonymousUser
	public void deletePostingAsAnyoneTest() throws Exception {
		postingTest.testEntityDelete(false, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
		postingTest.testEntityDelete(false, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_ADMIN)));
	}

	@Test
	@WithMockUser(username = "someUser", roles = { WebSecurityConfig.ROLE_NORMAL })
	public void deletePostingAsNormalTest() throws Exception {
		postingTest.testEntityDelete(false, postingWithCreator(userWithUsername("otherUser")));
		postingTest.testEntityDelete(true, postingWithCreator(userWithUsername("someUser")));
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void deletePostingAsAdminTest() throws Exception {
		postingTest.testEntityDelete(true, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
		postingTest.testEntityDelete(true, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_ADMIN)));
	}

	/*-
	 * #############################################
	 * posting get
	 * #############################################
	 */

	@Test
	@WithAnonymousUser
	public void getPostingAsAnyoneTest() throws Exception {
		postingTest.testEntityGet(true, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
		postingTest.testEntityGet(true, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_ADMIN)));
	}

	@Test
	@WithMockUser(username = "normalUser", roles = { WebSecurityConfig.ROLE_NORMAL })
	public void getPostingAsNormalUser() throws Exception {
		postingTest.testEntityGet(true, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
		postingTest.testEntityGet(true, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_ADMIN)));
	}

	@Test
	@WithMockUser(roles = { WebSecurityConfig.ROLE_ADMIN })
	public void getPostingAsAdminTest() throws Exception {
		postingTest.testEntityGet(true, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_NORMAL)));
		postingTest.testEntityGet(true, postingWithCreator(userWithRole(WebSecurityConfig.ROLE_ADMIN)));
	}

	/*-
	 * #############################################
	 * helping stuff
	 * #############################################
	 */

	private User userWithRole(String role) {
		return userWithUsernameRole(UUID.randomUUID().toString(), role);
	}

	private User userWithUsernameRole(String username, String role) {
		return new User(username, "password", role, "forename", "surname", "email");
	}

	private User userWithUsername(String username) {
		return userWithUsernameRole(username, null);
	}

	private Posting postingWithCreator(User creator) {
		return new Posting(eMa.persistAndFlush(creator), LocalDateTime.now(), "some text");
	}

}
