package com.nutrinfomics.geneway.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PasswordUtilsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private PasswordUtils passwordUtils = new PasswordUtils();
	private static final String PASSWORD = "24.12";

	@Test
	public void hashPassword_AsExpected() {
		assertNotEquals(PASSWORD, passwordUtils.hashPassword(PASSWORD));
	}

	@Test
	public void checkPassword_AsExpected() {
		assertTrue(passwordUtils.checkPassword(PASSWORD, PASSWORD));
	}

	@Test
	public void checkPassword_NullPass_False() {
		assertFalse(passwordUtils.checkPassword(PASSWORD, null));
	}

	@Test
	public void checkPassword_WrongPass_False() {
		assertFalse(passwordUtils.checkPassword(PASSWORD, "e"));
	}

	@Test
	public void checkPassword_NullHash_False() {
		assertFalse(passwordUtils.checkPassword(PASSWORD, null));
	}

	@Test
	public void checkHashedPassword_AsExpected() {
		String hashedPassword = passwordUtils.hashPassword(PASSWORD);
		assertTrue(passwordUtils.checkHashedPassword(PASSWORD, hashedPassword));
	}

	@Test
	public void checkHashedPassword_NullHash_False() {
		assertFalse(passwordUtils.checkHashedPassword(PASSWORD, null));
	}

	@Test
	public void checkHashedPassword_WrongHash_False() {
		thrown.expect(IllegalArgumentException.class);
		passwordUtils.checkHashedPassword(PASSWORD, "l");
	}

}
