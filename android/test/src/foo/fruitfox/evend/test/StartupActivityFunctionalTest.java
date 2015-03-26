package foo.fruitfox.evend.test;

import java.io.File;

import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import foo.fruitfox.data.UserData;
import foo.fruitfox.evend.StartupActivity;
import foo.fruitfox.evend.WelcomeActivity;
import foo.fruitfox.helpers.StorageHelper;

public class StartupActivityFunctionalTest extends
		ActivityInstrumentationTestCase2<StartupActivity> {

	private UserData userData;
	private String identifier;
	private StartupActivity startupActivity;

	public StartupActivityFunctionalTest() {
		super(StartupActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

		Context context = getInstrumentation().getTargetContext()
				.getApplicationContext();

		String originalFilePath = context.getFilesDir().getParent() + "/"
				+ "shared_prefs/UserData.xml";
		String backupFilePath = context.getFilesDir().getParent() + "/"
				+ "shared_prefs/UserData.xml.Backup";
		File originalPrefFile = new File(originalFilePath);
		File backupPrefFile = new File(backupFilePath);

		if (originalPrefFile.exists()) {
			originalPrefFile.renameTo(backupPrefFile);
		}

		userData = new UserData("email", "jane@example.com");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		Context context = getInstrumentation().getTargetContext()
				.getApplicationContext();

		String originalFilePath = context.getFilesDir().getParent() + "/"
				+ "shared_prefs/UserData.xml";
		String backupFilePath = context.getFilesDir().getParent() + "/"
				+ "shared_prefs/UserData.xml.Backup";
		File originalPrefFile = new File(originalFilePath);
		File backupPrefFile = new File(backupFilePath);
		originalPrefFile.delete();

		if (backupPrefFile.exists()) {
			backupPrefFile.renameTo(originalPrefFile);
		}
	}

	public void testOnCreateWithVerifiedUserData() throws Exception {
		Context context = getInstrumentation().getTargetContext();
		identifier = "jane@example.com";
		userData.setIsVerified(true);

		StorageHelper.PreferencesHelper.setIdentifier(context, identifier);
		StorageHelper.PreferencesHelper.setUserData(context, identifier,
				userData);

		// Add monitor to check for the welcome activity
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				WelcomeActivity.class.getName(), null, false);

		startupActivity = getActivity();

		// Wait 2 seconds for the start of the activity
		WelcomeActivity welcomeActivity = (WelcomeActivity) monitor
				.waitForActivityWithTimeout(2000);

		startupActivity.finish();

		assertNotNull(welcomeActivity);
	}

	public void testOnCreateWithUnverifiedUserData() throws Exception {
		Context context = getInstrumentation().getTargetContext();
		identifier = "jane@example.com";
		userData.setIsVerified(false);

		StorageHelper.PreferencesHelper.setIdentifier(context, identifier);
		StorageHelper.PreferencesHelper.setUserData(context, identifier,
				userData);

		// Add monitor to check for the welcome activity
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				WelcomeActivity.class.getName(), null, false);

		startupActivity = getActivity();

		// Wait 2 seconds for the start of the activity
		WelcomeActivity welcomeActivity = (WelcomeActivity) monitor
				.waitForActivityWithTimeout(2000);

		startupActivity.finish();

		assertNull(welcomeActivity);
	}

	public void testOnCreateWithNoUserData() throws Exception {
		Context context = getInstrumentation().getTargetContext();
		identifier = null;

		StorageHelper.PreferencesHelper.setIdentifier(context, identifier);

		// Add monitor to check for the welcome activity
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				WelcomeActivity.class.getName(), null, false);

		startupActivity = getActivity();

		// Wait 2 seconds for the start of the activity
		WelcomeActivity welcomeActivity = (WelcomeActivity) monitor
				.waitForActivityWithTimeout(2000);

		startupActivity.finish();

		assertNull(welcomeActivity);
	}
}
