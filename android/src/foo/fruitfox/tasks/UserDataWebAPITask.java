package foo.fruitfox.tasks;

import android.content.Context;
import android.os.AsyncTask;
import foo.fruitfox.helpers.NetworkHelper;

public class UserDataWebAPITask extends AsyncTask<String, Integer, String[]> {
	private AsyncResponseListener callback;

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

	public UserDataWebAPITask(Context context, AsyncResponseListener callback) {
		super();
		this.callback = callback;
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
	}
}
