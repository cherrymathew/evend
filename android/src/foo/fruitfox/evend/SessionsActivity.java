package foo.fruitfox.evend;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import foo.fruitfox.adapters.SessionsAdapter;
import foo.fruitfox.adapters.SessionsAdapter.ViewHolder;
import foo.fruitfox.data.SessionData;
import foo.fruitfox.helpers.DebugHelper;

public class SessionsActivity extends ActionBarActivity {

	ArrayList<SessionData> sessions = new ArrayList<SessionData>();
	ListView sessionsList;
	SessionsAdapter sa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);

		sessionsList = (ListView) findViewById(R.id.sessionsList);

		for (int i = 0; i < 5; i++) {
			SessionData session = new SessionData("Session " + i,
					"http://www.google.com/" + i, "http://www.bing.com/" + i);
			sessions.add(session);
		}

		sa = new SessionsAdapter(this, sessions);

		sessionsList.setAdapter(sa);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sessions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onPause() {
		super.onPause();
		DebugHelper.ShowMessage.d("Paused in Sessions");
	}

	public void onResume() {
		super.onResume();
		DebugHelper.ShowMessage.d("Resumed in Sessions");
		SessionData session = new SessionData("Session From resumed",
				"http://www.google.com/From resumed",
				"http://www.bing.com/From resumed");
		sessions.add(session);
		sa.notifyDataSetChanged();
	}

	public void sessionEdit(View view) {
		RelativeLayout currentRow = (RelativeLayout) view.getParent();
		SessionsAdapter.ViewHolder viewHolder = (ViewHolder) currentRow
				.getTag();
		DebugHelper.ShowMessage.t(this, viewHolder.editLink);
		Uri webpage = Uri.parse(viewHolder.editLink);
		Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
		startActivity(webIntent);
	}

	public void sessionView(View view) {
		RelativeLayout currentRow = (RelativeLayout) view.getParent();
		SessionsAdapter.ViewHolder viewHolder = (ViewHolder) currentRow
				.getTag();
		DebugHelper.ShowMessage.t(this, viewHolder.viewLink);
		Uri webpage = Uri.parse(viewHolder.viewLink);
		Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
		startActivity(webIntent);
	}
}
