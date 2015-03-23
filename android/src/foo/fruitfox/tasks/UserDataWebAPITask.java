package foo.fruitfox.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import foo.fruitfox.helpers.NetworkHelper;

public class UserDataWebAPITask extends AsyncTask<String, Integer, String[]> {
	private AsyncResponse ar;

	public interface AsyncResponse {
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

	public UserDataWebAPITask(Activity activity) {
		super();
		this.ar = (AsyncResponse) activity;
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
		String responseBody = result[1];
		String responseCode = result[0];

		ar.postAsyncTaskCallback(responseBody, responseCode);
	}
}
