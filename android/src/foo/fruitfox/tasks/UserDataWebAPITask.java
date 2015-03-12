package foo.fruitfox.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;

public class UserDataWebAPITask extends AsyncTask<String, Integer, String> {
	private ProgressDialog progDialog;
	private Context context;
	private Activity activity;
	private AsyncResponse ar;

	public interface AsyncResponse {
		void postAsyncTaskCallback(String result);
	}

	/**
	 * Construct a task
	 * 
	 * @param activity
	 */
	public UserDataWebAPITask(Activity activity) {
		super();
		this.activity = activity;
		this.context = this.activity.getApplicationContext();
		this.ar = (AsyncResponse) activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progDialog = ProgressDialog.show(this.activity, "Processing...",
				"Fetching data", true, false);
	}

	@Override
	protected String doInBackground(String... params) {
		String result = "";

		if (params[0] == "GET") {
			result = NetworkHelper.Utilities.HTTPGet(params[1]);
		} else if (params[0] == "POST") {
			result = NetworkHelper.Utilities.HTTPPost(params[1], params[2]);
		}

		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		progDialog.dismiss();
		if (result.length() == 0) {
			DebugHelper.ShowMessage
					.t(context,
							"There was an error processing your request. Please try again later.");
			return;
		} else {
			ar.postAsyncTaskCallback(result);
			return;
		}

	}
}
