package foo.fruitfox.tasks.test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.test.InstrumentationTestCase;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.server.UnitTestHTTPd;
import foo.fruitfox.tasks.UserDataWebAPITask;
import foo.fruitfox.tasks.UserDataWebAPITask.AsyncResponseListener;

public class UserDataWebAPITaskTest extends InstrumentationTestCase implements
		AsyncResponseListener {

	Context context;
	UnitTestHTTPd server;
	UserDataWebAPITask udwTask;
	CountDownLatch signal;

	String responseBody;
	String responseCode;

	public UserDataWebAPITaskTest() {

	}

	protected void setUp() throws Exception {
		super.setUp();

		responseBody = "";
		responseCode = "";

		context = getInstrumentation().getContext();
		server = new UnitTestHTTPd();
		udwTask = new UserDataWebAPITask(context, this);

		try {
			server.start();
		} catch (IOException e) {
			DebugHelper.ShowMessage.d(e.getLocalizedMessage());
		}
	}

	protected void tearDown() throws Exception {
		server.stop();
	}

	public void testUserDataWebAPITaskConstructor1() {
		UserDataWebAPITask testUdwTask = new UserDataWebAPITask(context, this);

		assertNotNull("udwTask is null", testUdwTask);
	}

	public void testUserDataWebAPITaskExecuteGet() throws Throwable {
		signal = new CountDownLatch(1);

		assertEquals("responseBody is not empty", "", responseBody);
		assertEquals("responseCode is not empty", "", responseCode);

		// Execute the async task on the UI thread! THIS IS KEY!
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				udwTask.execute("GET",
						"http://localhost:8080/testQueryString?fname=John&lname=Doe");
			}
		});

		signal.await(30, TimeUnit.SECONDS);

		assertEquals("responseBody does not match", "Hello, John Doe",
				responseBody);
		assertEquals("responseCode does not match", "200", responseCode);
	}

	public void testUserDataWebAPITaskExecutePost() throws Throwable {
		signal = new CountDownLatch(1);

		assertEquals("responseBody is not empty", "", responseBody);
		assertEquals("responseCode is not empty", "", responseCode);

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				udwTask.execute("POST", "http://localhost:8080/testPost",
						"John Doe");
			}
		});

		signal.await(30, TimeUnit.SECONDS);

		assertEquals("responseBody does not match", "Hello, John Doe",
				responseBody);
		assertEquals("responseCode does not match", "200", responseCode);
	}

	@Override
	public void postAsyncTaskCallback(String responseBody, String responseCode) {
		this.responseBody = responseBody;
		this.responseCode = responseCode;

		signal.countDown();
	}
}
