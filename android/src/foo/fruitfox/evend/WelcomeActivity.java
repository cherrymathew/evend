package foo.fruitfox.evend;

import java.util.Arrays;
import java.util.TimeZone;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.TextView;
import foo.fruitfox.adapters.EventCalendarAdapter;
import foo.fruitfox.helpers.DebugHelper;

public class WelcomeActivity extends ActionBarActivity implements
		OnItemClickListener, OnCheckedChangeListener {

	GridView eventCalendarGrid;
	CheckBox accomodationCheck;
	CheckBox pickupCheck;

	Boolean[] eventDays = null;
	Boolean[] stayAndPickup = new Boolean[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		accomodationCheck = (CheckBox) findViewById(R.id.accomodationCheck);
		pickupCheck = (CheckBox) findViewById(R.id.pickupCheck);
		eventCalendarGrid = (GridView) findViewById(R.id.eventCalendarGrid);

		EventCalendarAdapter eca = new EventCalendarAdapter(this, "2015-05-23",
				"2015-06-07");
		eventDays = new Boolean[eca.getCount()];

		eventCalendarGrid.setAdapter(eca);
		eventCalendarGrid.setOnItemClickListener(this);

		accomodationCheck.setOnCheckedChangeListener(this);
		pickupCheck.setOnCheckedChangeListener(this);

		DebugHelper.ShowMessage.d(" ECA Adapter",
				Integer.toString(eca.getCount()));
		DebugHelper.ShowMessage.t(this, TimeZone.getDefault().getID());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		DebugHelper.ShowMessage.t(this, "Clicked " + position);
		TextView dateText = (TextView) view.findViewById(R.id.dayText);
		ColorDrawable backgroundColor = (ColorDrawable) dateText
				.getBackground();

		// For API less than 11

		// if (backgroundColor.getC != null) {
		// Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		// Canvas canvas = new Canvas(bitmap);
		// backgroundColor.draw(canvas);
		//
		// if (bitmap.getPixel(0, 0) == Color.GREEN) {
		// dateText.setBackgroundColor(Color.TRANSPARENT);
		// } else {
		// dateText.setBackgroundColor(Color.GREEN);
		// }
		// } else {
		// dateText.setBackgroundColor(Color.GREEN);
		// }

		if (backgroundColor != null) {
			if (backgroundColor.getColor() == Color.GREEN) {
				dateText.setBackgroundColor(Color.TRANSPARENT);
				eventDays[position] = false;
			} else {
				dateText.setBackgroundColor(Color.GREEN);
				eventDays[position] = true;
			}
		} else {
			dateText.setBackgroundColor(Color.GREEN);
			eventDays[position] = true;
		}
		DebugHelper.ShowMessage.d(Arrays.toString(eventDays));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

		switch (buttonView.getId()) {
		case R.id.accomodationCheck:
			stayAndPickup[0] = isChecked;
			break;

		case R.id.pickupCheck:
			stayAndPickup[1] = isChecked;
			break;

		default:
			break;

		}

		DebugHelper.ShowMessage.d(Arrays.toString(stayAndPickup));
	}

	public void next(View view) {
		Intent intent = new Intent(this, SessionsActivity.class);
		startActivity(intent);
	}
}
