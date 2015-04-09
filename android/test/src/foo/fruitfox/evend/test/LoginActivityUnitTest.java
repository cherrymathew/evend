package foo.fruitfox.evend.test;

import java.util.concurrent.CountDownLatch;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import foo.fruitfox.evend.LoginActivity;
import foo.fruitfox.evend.R;
import foo.fruitfox.tasks.UserDataWebAPITask.AsyncJobNotifier;

public class LoginActivityUnitTest extends ActivityUnitTestCase<LoginActivity> {

	private Instrumentation instrumentation;
	private LoginActivity loginActivity;
	private Intent intent;

	private TextView usernameLabel;
	private EditText username;
	private Button login;
	private Button register;
	private Button verify;
	private TextView verificationCodeLabel;
	private TextView verificationCode;
	private LinearLayout usernameLayout;
	private LinearLayout buttonsLayout;
	private LinearLayout verificationLayout;

	public LoginActivityUnitTest() {
		super(LoginActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		instrumentation = getInstrumentation();
		ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(
				getInstrumentation().getTargetContext(), R.style.AppTheme);
		setActivityContext(contextThemeWrapper);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPreconditions() {
		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "email");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		usernameLabel = (TextView) loginActivity
				.findViewById(R.id.usernameLabel);
		username = (EditText) loginActivity.findViewById(R.id.username);
		login = (Button) loginActivity.findViewById(R.id.login);
		register = (Button) loginActivity.findViewById(R.id.register);
		verify = (Button) loginActivity.findViewById(R.id.verify);
		verificationCodeLabel = (TextView) loginActivity
				.findViewById(R.id.verificationCodeLabel);
		verificationCode = (TextView) loginActivity
				.findViewById(R.id.verificationCode);

		usernameLayout = (LinearLayout) loginActivity
				.findViewById(R.id.usernameLayout);
		buttonsLayout = (LinearLayout) loginActivity
				.findViewById(R.id.buttonsLayout);
		verificationLayout = (LinearLayout) loginActivity
				.findViewById(R.id.verificationLayout);

		loginActivity.finish();

		assertNotNull("loginActivity is null", loginActivity);
		assertNotNull("usernameLabel is null", usernameLabel);
		assertNotNull("username is null", username);
		assertNotNull("login is null", login);
		assertNotNull("register is null", register);
		assertNotNull("verify is null", verify);
		assertNotNull("verificationCodeLabel is null", verificationCodeLabel);
		assertNotNull("verificationCode is null", verificationCode);
		assertNotNull("usernameLayout is null", usernameLayout);
		assertNotNull("buttonsLayout is null", buttonsLayout);
		assertNotNull("verificationLayout is null", verificationLayout);
	}

	public void testUsernameOnIntentParameterEmail() {
		Intent intentEmail = new Intent(
				getInstrumentation().getTargetContext(), LoginActivity.class);
		intentEmail.putExtra("type", "email");

		startActivity(intentEmail, null, null);

		loginActivity = getActivity();

		usernameLabel = (TextView) loginActivity
				.findViewById(R.id.usernameLabel);

		String data = usernameLabel.getText().toString();

		loginActivity.finish();

		assertEquals("usernameLabel did not match", "E-Mail", data);
	}

	public void testUsernameOnIntentParameterPhone() {
		Intent intentPhone = new Intent(
				getInstrumentation().getTargetContext(), LoginActivity.class);
		intentPhone.putExtra("type", "phone");

		startActivity(intentPhone, null, null);

		loginActivity = getActivity();

		usernameLabel = (TextView) loginActivity
				.findViewById(R.id.usernameLabel);

		String data = usernameLabel.getText().toString();

		loginActivity.finish();

		assertEquals("usernameLabel did not match", "Phone Number", data);
	}

	public void testUsernameLayoutViews() {
		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "email");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		usernameLayout = (LinearLayout) loginActivity
				.findViewById(R.id.usernameLayout);

		usernameLabel = (TextView) usernameLayout
				.findViewById(R.id.usernameLabel);
		username = (EditText) usernameLayout.findViewById(R.id.username);

		assertNotNull("usernameLabel is null", usernameLabel);
		assertNotNull("username is null", username);
	}

	public void testButtonsLayoutViews() {
		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "email");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		buttonsLayout = (LinearLayout) loginActivity
				.findViewById(R.id.buttonsLayout);

		login = (Button) buttonsLayout.findViewById(R.id.login);
		register = (Button) buttonsLayout.findViewById(R.id.register);

		assertNotNull("login is null", login);
		assertNotNull("register is null", register);
	}

	public void testVerificationLayoutViews() {
		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "email");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		verificationLayout = (LinearLayout) loginActivity
				.findViewById(R.id.verificationLayout);

		verify = (Button) verificationLayout.findViewById(R.id.verify);
		verificationCodeLabel = (TextView) verificationLayout
				.findViewById(R.id.verificationCodeLabel);
		verificationCode = (TextView) verificationLayout
				.findViewById(R.id.verificationCode);

		assertNotNull("verify is null", verify);
		assertNotNull("verificationCode is null", verificationCode);
		assertNotNull("verificationCodeLabel is null", verificationCodeLabel);
	}

	public void testLayoutVisbility() {
		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "email");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		usernameLayout = (LinearLayout) loginActivity
				.findViewById(R.id.usernameLayout);
		buttonsLayout = (LinearLayout) loginActivity
				.findViewById(R.id.buttonsLayout);
		verificationLayout = (LinearLayout) loginActivity
				.findViewById(R.id.verificationLayout);

		assertEquals("usernameLayout is not visible", View.VISIBLE,
				usernameLayout.getVisibility());
		assertEquals("buttonsLayout is not visible", View.VISIBLE,
				buttonsLayout.getVisibility());
		assertEquals("verificationLayout is not gone", View.GONE,
				verificationLayout.getVisibility());
	}

	public void testButtonVisibility() {
		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "email");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		login = (Button) loginActivity.findViewById(R.id.login);
		register = (Button) loginActivity.findViewById(R.id.register);

		assertEquals("register button is not visible", View.VISIBLE,
				register.getVisibility());
		assertEquals("login button is not gone", View.GONE,
				login.getVisibility());
	}

	public void testUserNameTextIntentEmail() {
		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "email");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		username = (EditText) loginActivity.findViewById(R.id.username);

		assertEquals("username is not enabled", true, username.isEnabled());
		assertEquals("username text value did not match", "", username
				.getText().toString());
	}

	public void testUserNameTextIntentPhone() {
		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "phone");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		username = (EditText) loginActivity.findViewById(R.id.username);

		assertEquals("username is not enabled", true, username.isEnabled());
		assertEquals("username text value did not match", "+".charAt(0),
				username.getText().toString().charAt(0));
	}

	public void testServerURLAccessors() {
		String text = "foo";

		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "phone");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		loginActivity.setServerURL(text);

		assertEquals("serverURL did not match", text,
				loginActivity.getServerURL());
	}

	public void testUnitTestNotifierAccessors() {
		// create a signal to let us know when our task is done.
		final CountDownLatch signal = new CountDownLatch(1);

		intent = new Intent(getInstrumentation().getTargetContext(),
				LoginActivity.class);
		intent.putExtra("type", "phone");

		startActivity(intent, null, null);

		loginActivity = getActivity();

		loginActivity.setUnitTestNotifier(new AsyncJobNotifier() {
			@Override
			public void executionDone() {
				signal.countDown();
			}
		});

		loginActivity.getUnitTestNotifier().executionDone();

		assertEquals("AsyncJobNotifier did not execute executeDone() method",
				0, signal.getCount());
	}
}
