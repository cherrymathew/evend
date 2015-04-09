package foo.fruitfox.tasks;

import android.content.Context;
import android.os.AsyncTask;
import foo.fruitfox.helpers.NetworkHelper;

public class UserDataWebAPITask extends AsyncTask<String, Integer, String[]> {
	private AsyncResponseListener callback;
	private AsyncJobNotifierAccessors unitTestNotifierAccessors;

	/**
	 * 
	 * An activity using this Async Task class to execute it's HTTP requests in
	 * background should implement this interface if they wish to get the
	 * response of the executed HTTP methods
	 * 
	 */
	public interface AsyncResponseListener {
		/**
		 * 
		 * @param responseBody
		 *            is the JSON Response string after a HTTP callback is
		 *            completed.
		 * 
		 * @param responseCode
		 *            is the HTTP response code of the operation that was just
		 *            performed.
		 */
		void postAsyncTaskCallback(String responseBody, String responseCode);
	}

	/**
	 * 
	 * A functional "test" wishing to test an activity that does Async Tasks
	 * must implement this interface so that the notification can get through
	 * and the CountDownLatch object
	 * 
	 */
	public interface AsyncJobNotifier {
		void executionDone();
	}

	/**
	 * 
	 * An activity undergoing functional tests which utilizes this Async Task
	 * class, should implement this interface so that the functional test is
	 * notified when the Async Job is completed
	 * 
	 */
	public interface AsyncJobNotifierAccessors {
		public AsyncJobNotifier getUnitTestNotifier();

		public void setUnitTestNotifier(AsyncJobNotifier unitTestNotifier);
	}

	public UserDataWebAPITask(Context context, AsyncResponseListener callback,
			AsyncJobNotifierAccessors notifierAccessors) {
		super();
		this.callback = callback;
		this.unitTestNotifierAccessors = notifierAccessors;
	}

	public UserDataWebAPITask(Context context, AsyncResponseListener callback) {
		super();
		this.callback = callback;
		this.unitTestNotifierAccessors = null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String[] doInBackground(String... params) {
		String[] result = new String[2];

		if (params[0] == "GET") {
			result = NetworkHelper.Utilities.HTTPGet(params[1]);
		} else if (params[0] == "POST") {
			result = NetworkHelper.Utilities.HTTPPost(params[1], params[2]);
		}

		return result;
	}

	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		String responseBody = result[1];
		String responseCode = result[0];

		callback.postAsyncTaskCallback(responseBody, responseCode);

		if (unitTestNotifierAccessors != null
				&& unitTestNotifierAccessors.getUnitTestNotifier() != null) {
			unitTestNotifierAccessors.getUnitTestNotifier().executionDone();
		}
	}
}
