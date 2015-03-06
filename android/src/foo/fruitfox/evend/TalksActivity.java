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
import foo.fruitfox.adapters.TalksAdapter;
import foo.fruitfox.adapters.TalksAdapter.ViewHolder;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.helpers.DebugHelper;

public class TalksActivity extends ActionBarActivity {

	ArrayList<TalkData> talks = new ArrayList<TalkData>();
	ListView talksList;
	TalksAdapter ta;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talks);

		talksList = (ListView) findViewById(R.id.talksList);

		for (int i = 0; i < 5; i++) {
			TalkData session = new TalkData("Talk " + i,
					"http://www.google.com/" + i, "http://www.bing.com/" + i);
			talks.add(session);
		}

		ta = new TalksAdapter(this, talks);

		talksList.setAdapter(ta);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.talks, menu);
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
		DebugHelper.ShowMessage.d("Paused in Talks");
	}

	public void onResume() {
		super.onResume();
		DebugHelper.ShowMessage.d("Resumed in Talks");
		TalkData session = new TalkData("Talk From resumed",
				"http://www.google.com/From resumed",
				"http://www.bing.com/From resumed");
		talks.add(session);
		ta.notifyDataSetChanged();
	}

	public void talkEdit(View view) {
		RelativeLayout currentRow = (RelativeLayout) view.getParent();
		TalksAdapter.ViewHolder viewHolder = (ViewHolder) currentRow.getTag();
		DebugHelper.ShowMessage.t(this, viewHolder.editLink);
		Uri webpage = Uri.parse(viewHolder.editLink);
		Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
		startActivity(webIntent);
	}

	public void talkView(View view) {
		RelativeLayout currentRow = (RelativeLayout) view.getParent();
		TalksAdapter.ViewHolder viewHolder = (ViewHolder) currentRow.getTag();
		DebugHelper.ShowMessage.t(this, viewHolder.viewLink);
		Uri webpage = Uri.parse(viewHolder.viewLink);
		Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
		startActivity(webIntent);
	}
}
