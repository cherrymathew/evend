package foo.fruitfox.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import foo.fruitfox.evend.MainActivity;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;

public class UserDataWebAPITask extends AsyncTask<String, Integer, String> {
	private ProgressDialog progDialog;
	private Context context;
	private MainActivity activity;

	// TODO: Need to do something about Activity class type

	/**
	 * Construct a task
	 * 
	 * @param activity
	 */
	public UserDataWebAPITask(MainActivity activity) {
		super();
		this.activity = activity;
		this.context = this.activity.getApplicationContext();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progDialog = ProgressDialog.show(this.activity, "Search",
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
			DebugHelper.ShowMessage.t(context,
					"Unable to find track data. Try again later.");
			return;
		} else {
			DebugHelper.ShowMessage.t(context, result);
			return;
		}

	}
}
