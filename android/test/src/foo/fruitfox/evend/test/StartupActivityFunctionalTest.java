package foo.fruitfox.evend.test;

import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import foo.fruitfox.data.UserData;
import foo.fruitfox.evend.StartupActivity;
import foo.fruitfox.evend.WelcomeActivity;
import foo.fruitfox.helpers.StorageHelper;

public class StartupActivityFunctionalTest extends
		ActivityInstrumentationTestCase2<StartupActivity> {

	private UserData currentUserData;
	private String currentIdentifier;
	private UserData userData;
	private String identifier;
	private StartupActivity startupActivity;

	public StartupActivityFunctionalTest() {
		super(StartupActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

		Context context = getInstrumentation().getTargetContext();
		currentIdentifier = StorageHelper.PreferencesHelper
				.getIdentifier(context);
		if (currentIdentifier != null) {
			currentUserData = StorageHelper.PreferencesHelper.getUserData(
					context, currentIdentifier);
		}
		userData = new UserData("email", "john@example.com");
	}

	public void testOnCreateWithVerifiedUserData() throws Exception {
		Context context = getInstrumentation().getTargetContext();
		identifier = "john@example.com";
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
		identifier = "john@example.com";
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

	protected void tearDown() throws Exception {
		super.tearDown();
		Context context = getInstrumentation().getTargetContext();
		StorageHelper.PreferencesHelper.clearAllUserData(context);

		if (currentIdentifier != null) {
			StorageHelper.PreferencesHelper.setIdentifier(context,
					currentIdentifier);
			StorageHelper.PreferencesHelper.setUserData(context,
					currentIdentifier, currentUserData);
		}
	}
}
