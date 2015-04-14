package foo.fruitfox.tasks;

import java.io.IOException;

import org.schwering.irc.lib.IRCConnection;

import android.content.Context;
import android.os.AsyncTask;
import foo.fruitfox.helpers.DebugHelper;

public class IRCChatAPITask extends AsyncTask<String, Integer, IRCConnection> {
	private AsyncResponseListener callback;
	private AsyncJobNotifierAccessors unitTestNotifierAccessors;
	private IRCConnection connection;

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
		 * @param connection
		 *            is the IRCConnection object that has been attempted to
		 *            establish a connection via this Async Task.
		 * 
		 *            There is no guarentee that the connection is alive if the
		 *            connection has to be explicitly checked before doing any
		 *            activity related with the client
		 */
		void postAsyncTaskCallback(IRCConnection connection);
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

	public IRCChatAPITask(Context context, IRCConnection connection,
			AsyncResponseListener callback,
			AsyncJobNotifierAccessors notifierAccessors) {
		super();
		this.connection = connection;
		this.callback = callback;
		this.unitTestNotifierAccessors = notifierAccessors;
	}

	public IRCChatAPITask(Context context, IRCConnection connection,
			AsyncResponseListener callback) {
		super();
		this.callback = callback;
		this.connection = connection;
		this.unitTestNotifierAccessors = null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected IRCConnection doInBackground(String... params) {
		if (connection != null) {
			try {
				connection.connect();
			} catch (IOException e) {
				DebugHelper.ShowMessage.d(e.getMessage());
			}
		}
		return connection;
	}

	@Override
	protected void onPostExecute(IRCConnection result) {
		super.onPostExecute(result);

		callback.postAsyncTaskCallback(result);

		if (unitTestNotifierAccessors != null
				&& unitTestNotifierAccessors.getUnitTestNotifier() != null) {
			unitTestNotifierAccessors.getUnitTestNotifier().executionDone();
		}
	}
}
