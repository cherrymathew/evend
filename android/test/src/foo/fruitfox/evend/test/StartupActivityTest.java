package foo.fruitfox.evend.test;

import android.test.ActivityInstrumentationTestCase2;
import foo.fruitfox.evend.StartupActivity;

public class StartupActivityTest extends
		ActivityInstrumentationTestCase2<StartupActivity> {

	private StartupActivity startupActivity;

	public StartupActivityTest() {
		super(StartupActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		startupActivity = getActivity();
	}

	public void testPreconditions() {
		// Try to add a message to add context to your assertions. These
		// messages will be shown if a tests fails and make it easy to
		// understand why a test failed
		assertNotNull("startupActivity is null", startupActivity);
	}

}
