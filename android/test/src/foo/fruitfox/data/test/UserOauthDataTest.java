package foo.fruitfox.data.test;

import junit.framework.TestCase;
import foo.fruitfox.data.UserOauthData;

public class UserOauthDataTest extends TestCase {
	UserOauthData userOauthData;

	public UserOauthDataTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		userOauthData = new UserOauthData("", "", "");
	}

	public void testUserOauthDataConstructor1() {
		UserOauthData testUserOauthData = new UserOauthData("Provider",
				"AccessToken", "UserId");

		assertEquals("oauthProvider value did not match", "Provider",
				testUserOauthData.getOauthProvider());
		assertEquals("oauthAccessToken value did not match", "AccessToken",
				testUserOauthData.getOauthAccessToken());
		assertEquals("oauthUserId value did not match", "UserId",
				testUserOauthData.getOauthUserId());
	}

	public void testUserOauthDataMemberOauthProvider() {
		String oauthProvider = "Provider";

		userOauthData.setOauthProvider(oauthProvider);

		assertEquals("oauthProvider values did not match", oauthProvider,
				userOauthData.getOauthProvider());
	}

	public void testUserOauthDataMemberOauthAccessToken() {
		String oauthAccessToken = "AccessToken";

		userOauthData.setOauthAccessToken(oauthAccessToken);

		assertEquals("oauthProvider values did not match", oauthAccessToken,
				userOauthData.getOauthAccessToken());
	}

	public void testUserOauthDataMemberOauthUserId() {
		String oauthUserId = "UserId";

		userOauthData.setOauthUserId(oauthUserId);

		assertEquals("oauthUserId values did not match", oauthUserId,
				userOauthData.getOauthUserId());
	}
}
