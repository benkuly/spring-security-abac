package net.folivo.springframework.security.abac.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class RepoSecurityTestHelper<T> {

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
				? original != null && (originalId == null || eMa.find(original.getClass(), originalId) == null)
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
		testMethodSecurity(allowed, repo, "deleteById", originalId);
		assertEquals(expectedRepoSize, getRepoSize());
	}

	public void testEntityGet(boolean allowed, T original) throws Exception {
		eMa.persistAndFlush(original);
		boolean methodAllowed = SecurityContextHolder.getContext().getAuthentication().getPrincipal()
				.equals("anonymous") ? allowed : true;
		Optional<T> found = testMethodSecurity(methodAllowed, repo, "findById",
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
				fail("AccessDeniedException was thrown. With: " + e.getMessage());
		}
		return result;
	}

	public Long getRepoSize() {
		return eMa.getEntityManager().createQuery("SELECT COUNT(t) FROM " + clazzT.getSimpleName() + " t", Long.class)
				.getSingleResult();
	}

	public void clearRepository() {
		eMa.getEntityManager().createQuery("DELETE FROM " + clazzT.getSimpleName()).executeUpdate();
		eMa.flush();
	}

	public void compareEntities(Object one, Object two) {
		if (one == two || one.equals(two))
			return;
		Arrays.stream(one.getClass().getDeclaredFields()).map(f -> f.getName())
				.forEach(f -> assertEquals(ReflectionTestUtils.getField(one, f), ReflectionTestUtils.getField(two, f)));
	}

}
