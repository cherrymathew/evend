package foo.fruitfox.evend.test;

import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import foo.fruitfox.evend.R;
import foo.fruitfox.evend.StartupActivity;

public class StartupActivityUnitTest extends
		android.test.ActivityUnitTestCase<StartupActivity> {

	private StartupActivity startupActivity;
	private Button phoneVerification;
	private Button emailVerification;

	public StartupActivityUnitTest() {
		super(StartupActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(
				getInstrumentation().getTargetContext(), R.style.AppTheme);
		setActivityContext(contextThemeWrapper);

		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				StartupActivity.class);
		startActivity(intent, null, null);

		startupActivity = getActivity();

		phoneVerification = (Button) startupActivity
				.findViewById(R.id.phoneVerification);
		emailVerification = (Button) startupActivity
				.findViewById(R.id.emailVerification);
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		startupActivity.finish();
	}

	public void testPreconditions() {
		assertNotNull("startupActivity is null", startupActivity);
		assertNotNull("phoneVerification is null", phoneVerification);
		assertNotNull("emailVerification is null", emailVerification);
	}

	public void testPhoneVerificationButtonText() {
		assertEquals("Incorrect text for Phone Registration Button",
				"Phone Number Registration", phoneVerification.getText());
	}

	public void testEmailVerificationButtonText() {
		assertEquals("Incorrect text for Email Registration Button",
				"E-mail Registration", emailVerification.getText());
	}

	public void testPhoneVerificationButtonClick() {
		phoneVerification.performClick();

		Intent triggeredIntent = getStartedActivityIntent();
		assertNotNull("Intent was null", triggeredIntent);

		String data = triggeredIntent.getExtras().getString("type");

		assertEquals("Incorrect value recieved", "phone", data);
	}

	public void testEmailVerificationButtonClick() {
		emailVerification.performClick();

		Intent triggeredIntent = getStartedActivityIntent();
		assertNotNull("Intent was null", triggeredIntent);

		String data = triggeredIntent.getExtras().getString("type");

		assertEquals("Incorrect value recieved", "email", data);
	}
}
