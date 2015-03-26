package foo.fruitfox.helpers.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.data.UserOauthData;
import foo.fruitfox.helpers.StorageHelper;

public class StorageHelperTest extends InstrumentationTestCase {
	Context mockContext;
	Context actualContext;

	public StorageHelperTest() {
	}

	protected void setUp() throws Exception {
		System.setProperty("dexmaker.dexcache", getInstrumentation()
				.getTargetContext().getCacheDir().getPath());

		mockContext = Mockito.mock(Context.class);
		actualContext = getInstrumentation().getTargetContext();

		Mockito.when(
				mockContext.getSharedPreferences("UserData",
						Context.MODE_PRIVATE)).thenReturn(
				actualContext.getSharedPreferences("UserDataTest",
						Context.MODE_PRIVATE));

	}

	protected void tearDown() throws Exception {
		SharedPreferences sp = actualContext.getSharedPreferences(
				"UserDataTest", Context.MODE_PRIVATE);

		sp.edit().clear().commit();

		String filePath = actualContext.getFilesDir().getParent() + "/"
				+ "shared_prefs/UserDataTest.xml";
		File deletePrefFile = new File(filePath);
		deletePrefFile.delete();
	}

	public void testStorageHelperLoadStoreIdentifier() {
		String identifier = "someIdentifierString";

		StorageHelper.PreferencesHelper.setIdentifier(mockContext, identifier);

		String result = StorageHelper.PreferencesHelper
				.getIdentifier(mockContext);

		Mockito.verify(mockContext, Mockito.times(2)).getSharedPreferences(
				"UserData", Context.MODE_PRIVATE);

		assertEquals("identifier value did not match", identifier, result);

	}

	public void testStorageHelperLoadStoreUserData() {
		UserData originalUserData = new UserData("email", "john@example.com");
		UserData returnedUserData = null;
		List<TalkData> talkDataList = new ArrayList<TalkData>();
		String identifier = originalUserData.getEmail();

		for (int i = 0; i < 5; i++) {
			TalkData talkData = new TalkData("Title " + (i + 1), "2015-01-"
					+ (i + 1));
			talkDataList.add(talkData);
		}

		originalUserData.setTalkDataList(talkDataList);
		originalUserData.setFirstName("John");
		originalUserData.setLastName("Doe");
		originalUserData.setOauthData(new UserOauthData("Provider",
				"AccessToken", "UserId"));

		StorageHelper.PreferencesHelper.setIdentifier(mockContext, identifier);

		StorageHelper.PreferencesHelper.setUserData(mockContext, identifier,
				originalUserData);

		returnedUserData = StorageHelper.PreferencesHelper.getUserData(
				mockContext, identifier);

		Mockito.verify(mockContext, Mockito.times(3)).getSharedPreferences(
				"UserData", Context.MODE_PRIVATE);

		assertEquals("firstName value did not match",
				originalUserData.getFirstName(),
				returnedUserData.getFirstName());
		assertEquals("lastName value did not match",
				originalUserData.getLastName(), returnedUserData.getLastName());
		assertEquals("provider value did not match", originalUserData
				.getOauthData().getOauthProvider(), returnedUserData
				.getOauthData().getOauthProvider());
		assertEquals("talkTitle value did not match", originalUserData
				.getTalkDataList().get(0).getTitle(), returnedUserData
				.getTalkDataList().get(0).getTitle());
		assertEquals("talkDate value did not match", originalUserData
				.getTalkDataList().get(2).getDate("yyyy-MM-dd"),
				returnedUserData.getTalkDataList().get(2).getDate("yyyy-MM-dd"));
	}

	public void testStorageHelperClearData() {
		UserData originalUserData = new UserData("email", "john@example.com");
		UserData returnedUserData;
		String identifier = originalUserData.getEmail();

		StorageHelper.PreferencesHelper.setIdentifier(mockContext, identifier);

		StorageHelper.PreferencesHelper.setUserData(mockContext, identifier,
				originalUserData);

		StorageHelper.PreferencesHelper.clearAllUserData(mockContext);

		returnedUserData = StorageHelper.PreferencesHelper.getUserData(
				mockContext, identifier);

		Mockito.verify(mockContext, Mockito.times(4)).getSharedPreferences(
				"UserData", Context.MODE_PRIVATE);

		assertNull("returnedUserData is not null", returnedUserData);
	}
}
