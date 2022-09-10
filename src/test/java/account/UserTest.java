package account;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import utils.APNUtils;

//Blackbox
public class UserTest {

	@Test
	public void test_credentialsMatch() {
		User user1 = generateRandomUser();
		assertEquals(user1.credentialsMatch(user1), true);
		User user2 = generateRandomUser();
		assertEquals(user1.credentialsMatch(user2), false);
	}
	
	
	public static User generateRandomUser() {
		User u = new User();
		int rand = getRandomNumber(1,1000000);
		u.setUsername("rndtestusr"+rand);
		u.setPassword(APNUtils.getMd5("5f4dcc3b5aa7"));
		u.setRole(Roles.CONFIGURATOR);
		return u;
	}
	
	static int getRandomNumber(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max+1);
	}
	
}
