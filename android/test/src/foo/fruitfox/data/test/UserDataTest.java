package foo.fruitfox.data.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.data.UserOauthData;

public class UserDataTest extends TestCase {
	UserData userData;

	public UserDataTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		userData = new UserData("email", "john@example.com");
	}

	public void testUserDataConstructor1() {
		UserData testUserData = new UserData("email", "john@example.com");

		assertEquals("The email did not match", "john@example.com",
				testUserData.getEmail());
		assertEquals("email", testUserData.getRegistrationIdType());
		assertFalse(testUserData.getNeedsPickUp());
		assertFalse(testUserData.getNeedsAccommodation());
		assertFalse(testUserData.getHasTalk());
		assertFalse(testUserData.getHasOauth());
		assertFalse(testUserData.getIsVerified());
	}

	public void testUserDataConstrutor2() {
		UserData testUserData = new UserData("phone", "+919876543210");

		assertEquals("The phone did not match", "+919876543210",
				testUserData.getPhone());
		assertEquals("phone", testUserData.getRegistrationIdType());
		assertFalse(testUserData.getNeedsPickUp());
		assertFalse(testUserData.getNeedsAccommodation());
		assertFalse(testUserData.getHasTalk());
		assertFalse(testUserData.getHasOauth());
		assertFalse(testUserData.getIsVerified());
	}

	public void testUserDataConstructor3() {
		UserData testUserData = new UserData("Provider", "AccessToken",
				"john@example.com");

		assertEquals("Provider", testUserData.getOauthData().getOauthProvider());
		assertEquals("AccessToken", testUserData.getOauthData()
				.getOauthAccessToken());
		assertEquals("john@example.com", testUserData.getOauthData()
				.getOauthUserId());
		assertFalse(testUserData.getNeedsPickUp());
		assertFalse(testUserData.getNeedsAccommodation());
		assertFalse(testUserData.getHasTalk());
		assertTrue(testUserData.getHasOauth());
		assertFalse(testUserData.getIsVerified());
	}

	public void testUserDataMemberEmail() {
		String email = "jane@example.com";
		userData.setEmail(email);

		assertEquals("email values did not match", email, userData.getEmail());
	}

	public void testUserDataMemberPhone() {
		String phone = "+919876543210";
		userData.setPhone(phone);

		assertEquals("phone values did not match", phone, userData.getPhone());
	}

	public void testUserDataMemberRegistrationIdType() {
		String registrationIdType = "email";
		userData.setRegistrationIdType(registrationIdType);

		assertEquals("registrationIdType values did not match",
				registrationIdType, userData.getRegistrationIdType());
	}

	public void testUserDataMemberFirstName() {
		String firstName = "John";
		userData.setFirstName(firstName);

		assertEquals("firstName values did not match", firstName,
				userData.getFirstName());
	}

	public void testUserDataMemberLastName() {
		String lastName = "Doe";
		userData.setLastName(lastName);

		assertEquals("lastName values did not match", lastName,
				userData.getLastName());
	}

	public void testUserDataMethodFullName() {
		String firstName = "John";
		String lastName = "Doe";
		userData.setFirstName(firstName);
		userData.setLastName(lastName);

		assertEquals("fullName values did not match", firstName + " "
				+ lastName, userData.getFullName());

	}

	public void testUserDataMemberVerificationCode() {
		String verificationCode = "123456";
		userData.setVerificationCode(verificationCode);

		assertEquals("verificationCode values did not match", verificationCode,
				userData.getVerificationCode());
	}

	public void testUserDataMemberAuthtoken() {
		String authToken = "976kVrSBZA1XKccKw20PMAm4PYPHGACIC3dbSIiKHRIZuwh4XM5ECLRJiPScqd";
		userData.setAuthToken(authToken);

		assertEquals("authToken values did not match", authToken,
				userData.getAuthToken());
	}

	public void testUserDataMemberEventDaysAttending() {
		Boolean[] eventDaysAttending = new Boolean[10];
		Arrays.fill(eventDaysAttending, false);
		userData.setEventDaysAttending(eventDaysAttending);

		assertEquals("eventDaysAttending values did not match",
				eventDaysAttending, userData.getEventDaysAttending());
	}

	public void testUserDataMemberNeedsPickup() {
		Boolean needsPickUp = true;
		userData.setNeedsPickUp(needsPickUp);

		assertEquals("needsPickUp values did not match", needsPickUp,
				userData.getNeedsPickUp());
	}

	public void testUserDataMemberNeedsAccommodation() {
		Boolean needsAccommodation = true;
		userData.setNeedsAccommodation(needsAccommodation);

		assertEquals("needsAccommodation values did not match",
				needsAccommodation, userData.getNeedsAccommodation());
	}

	public void testUserDataMemberHasTalk() {
		Boolean hasTalk = true;
		userData.setHasTalk(hasTalk);

		assertEquals("hasTalk values did not match", hasTalk,
				userData.getHasTalk());
	}

	public void testUserDataMemberHasOauth() {
		Boolean hasOauth = false;
		userData.setHasOauth(hasOauth);

		assertEquals("hasOauth values did not match", hasOauth,
				userData.getHasOauth());
	}

	public void testUserDataMemberIsVerified() {
		Boolean isVerified = true;
		userData.setIsVerified(isVerified);

		assertEquals("isVerified values did not match", isVerified,
				userData.getIsVerified());
	}

	public void testUserDataMemberOauthData() {
		UserOauthData userOauthData = new UserOauthData("Provider",
				"AccessToken", "john@example.com");
		UserOauthData fromUserDataOauthData = null;
		userData.setOauthData(userOauthData);

		fromUserDataOauthData = userData.getOauthData();

		assertEquals("oauthProvider values did not match",
				userOauthData.getOauthProvider(),
				fromUserDataOauthData.getOauthProvider());
		assertEquals("oauthAccessToken values did not match",
				userOauthData.getOauthAccessToken(),
				fromUserDataOauthData.getOauthAccessToken());
		assertEquals("oauthUserId values did not match",
				userOauthData.getOauthUserId(),
				fromUserDataOauthData.getOauthUserId());
	}

	public void testUserDataMemberTalkData() {
		TalkData talkData = new TalkData("Talk Title", "Edit Link", "View Link");
		List<TalkData> talkDataList = new ArrayList<TalkData>();
		TalkData userTalkData = null;
		List<TalkData> userTalkDataList = null;
		talkDataList.add(talkData);

		userData.setTalkDataList(talkDataList);

		userTalkDataList = userData.getTalkDataList();
		userTalkData = userTalkDataList.get(0);

		assertEquals("List length mismatch", talkDataList.size(),
				userTalkDataList.size());
		assertEquals("title values did not match", talkData.getTitle(),
				userTalkData.getTitle());
		assertEquals("Edit link values did not match", talkData.getEditLink(),
				userTalkData.getEditLink());
		assertEquals("View link values did not match", talkData.getViewLink(),
				userTalkData.getViewLink());
	}
}
