package com.dhanush.app.ws.shared.dto.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
		String userId = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);
		
		assertNotNull(userId);
		assertNotNull(userId2);
		
		assertTrue(userId.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userId2));		
	}

	@Test
	void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("sjnjsbdfhbaf");
		assertNotNull(token);
		
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired); 
	}
	
	@Test
	void testHasTokenExpired() {
		String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkaGFudXNoa3VtYXJzaXZhamlAZ21haWwuY29tIiwiZXhwIjoxNTk5OTA3Nzc4fQ.kFS9i2bzaDowyRo8ABhQVv7aCe_yl-j0TV-hHELwH0fanjZMihKg65s6XgnMCZ9nycfY0tyylAiK_6mhHclplw";
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);
		assertTrue(hasTokenExpired); 
	}

}
