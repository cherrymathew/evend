package foo.fruitfox.helpers.test;

import java.io.IOException;

import org.mockito.Mockito;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.InstrumentationTestCase;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.server.UnitTestHTTPd;

public class NetworkHelperTest extends InstrumentationTestCase {

	UnitTestHTTPd server;

	public NetworkHelperTest() {
	}

	protected void setUp() throws Exception {
		System.setProperty("dexmaker.dexcache", getInstrumentation()
				.getTargetContext().getCacheDir().getPath());

		server = new UnitTestHTTPd();

		try {
			server.start();
		} catch (IOException e) {
			DebugHelper.ShowMessage.d(e.getLocalizedMessage());
		}
	}

	protected void tearDown() throws Exception {
		server.stop();
	}

	public void testNetworkHelperMethodIsConnectedTrue() {
		Context context = Mockito.mock(Context.class);
		ConnectivityManager connMgr = Mockito.mock(ConnectivityManager.class);
		NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);

		Mockito.when(context.getSystemService(Activity.CONNECTIVITY_SERVICE))
				.thenReturn(connMgr);
		Mockito.when(connMgr.getActiveNetworkInfo()).thenReturn(networkInfo);
		Mockito.when(networkInfo.isConnected()).thenReturn(true);

		Boolean status = NetworkHelper.Utilities.isConnected(context);

		Mockito.verify(context).getSystemService(Activity.CONNECTIVITY_SERVICE);
		Mockito.verify(connMgr).getActiveNetworkInfo();
		Mockito.verify(networkInfo).isConnected();

		assertTrue(status);
	}

	public void testNetworkHelperMethodIsConnectedFalse() {
		Context context = Mockito.mock(Context.class);
		ConnectivityManager connMgr = Mockito.mock(ConnectivityManager.class);
		NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);

		Mockito.when(context.getSystemService(Activity.CONNECTIVITY_SERVICE))
				.thenReturn(connMgr);
		Mockito.when(connMgr.getActiveNetworkInfo()).thenReturn(networkInfo);
		Mockito.when(networkInfo.isConnected()).thenReturn(false);

		Boolean status = NetworkHelper.Utilities.isConnected(context);

		Mockito.verify(context).getSystemService(Activity.CONNECTIVITY_SERVICE);
		Mockito.verify(connMgr).getActiveNetworkInfo();
		Mockito.verify(networkInfo).isConnected();

		assertFalse(status);
	}

	public void testHTTPGet() {
		String[] result;

		result = NetworkHelper.Utilities
				.HTTPGet("http://localhost:8080/test200");

		assertEquals("Response code did not match", "200",
				result[NetworkHelper.Utilities.RESPONSE_CODE]);
		assertEquals("Expected response did not match", "Hello, World!",
				result[NetworkHelper.Utilities.RESPONSE_JSON]);

		result = NetworkHelper.Utilities
				.HTTPGet("http://localhost:8080/test404");

		assertEquals("Response code did not match", "404",
				result[NetworkHelper.Utilities.RESPONSE_CODE]);
		assertEquals("Expected response did not match", "Not Found!",
				result[NetworkHelper.Utilities.RESPONSE_JSON]);

		result = NetworkHelper.Utilities
				.HTTPGet("http://localhost:8080/testQueryString?fname=John&lname=Doe");

		assertEquals("Response code did not match", "200",
				result[NetworkHelper.Utilities.RESPONSE_CODE]);
		assertEquals("Expected response did not match", "Hello, John Doe",
				result[NetworkHelper.Utilities.RESPONSE_JSON]);
	}

	public void testHTTPPost() {
		String[] result;

		result = NetworkHelper.Utilities.HTTPPost(
				"http://localhost:8080/testPost", "John Doe");

		assertEquals("Response code did not match", "200",
				result[NetworkHelper.Utilities.RESPONSE_CODE]);
		assertEquals("Expected response did not match", "Hello, John Doe",
				result[NetworkHelper.Utilities.RESPONSE_JSON]);
	}
}
