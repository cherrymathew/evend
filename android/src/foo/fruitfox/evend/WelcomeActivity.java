package foo.fruitfox.evend;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.UserDataWebAPITask;
import foo.fruitfox.tasks.UserDataWebAPITask.AsyncResponseListener;

public class WelcomeActivity extends ActionBarActivity implements
		OnItemClickListener, OnCheckedChangeListener, AsyncResponseListener {

	private GridView eventCalendarGrid;
	private CheckBox accommodationCheck;
	private CheckBox pickupCheck;
	private CheckBox talkCheck;

	private EventCalendarAdapter eventCalendarAdapter;

	private Boolean[] eventDays = null;
	private Boolean needsAccommodation = false;
	private Boolean needsPickup = false;
	private Boolean hasTalk = false;

	private String identifier;
	private UserData userData;

	private String startDateString = "2015-05-23";
	private String endDateString = "2015-06-07";

	private String serverURL;

	private ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		accommodationCheck = (CheckBox) findViewById(R.id.accommodationCheck);
		pickupCheck = (CheckBox) findViewById(R.id.pickupCheck);
		talkCheck = (CheckBox) findViewById(R.id.talkCheck);
		eventCalendarGrid = (GridView) findViewById(R.id.eventCalendarGrid);

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		serverURL = getResources().getString(R.string.server_url);

		initializeLayout();

		initializeListeners();

		initalizeAdapter();

		if (userData.getIsFinalized() == true) {
			Intent intent = new Intent(this, SummaryActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(intent);
			finish();
		}
		// getEventDaysUpdate();
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

	protected void onResume() {
		super.onResume();
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
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

		eventCalendarAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.accommodationCheck:
			needsAccommodation = isChecked;
			break;

		case R.id.pickupCheck:
			needsPickup = isChecked;
			break;

		case R.id.talkCheck:
			hasTalk = isChecked;
			break;

		default:
			break;

		}
	}

	public void next(View view) {
		String currentTimeZone = TimeZone.getDefault().getID();
		DateTime startDate = new DateTime(startDateString,
				DateTimeZone.forID(currentTimeZone));

		String dates = "";

		JSONObject requestJSON = new JSONObject();

		if (needsAccommodation == true) {
			userData.setNeedsAccommodation(true);
		} else {
			userData.setNeedsAccommodation(false);
			userData.setAccommodationData(null);
		}

		if (needsPickup == true) {
			userData.setNeedsPickUp(true);
		} else {
			userData.setNeedsPickUp(false);
			userData.setPickupData(null);
		}

		if (hasTalk == true) {
			userData.setHasTalk(true);
		} else {
			userData.setHasTalk(false);
			userData.setTalkDataList(new ArrayList<TalkData>());
		}

		userData.setEventDaysAttending(eventDays);

		for (int i = 0; i < eventDays.length; i++) {
			if (eventDays[i] == true) {
				dates += Integer
						.toString(startDate.plusDays(i).getDayOfMonth())
						+ "-"
						+ Integer.toString(startDate.plusDays(i)
								.getMonthOfYear() - 1)
						+ "-"
						+ Integer.toString(startDate.plusDays(i).getYear())
						+ ",";
			}
		}

		if (dates.length() > 0) {
			dates = dates.substring(0, dates.length() - 1);
		}

		try {
			requestJSON.put("hhtoken", userData.getAuthToken());
			requestJSON.put("dates", dates);
			requestJSON.put("accommodation",
					userData.getNeedsAccommodation() ? "1" : "0");
			requestJSON.put("pickup", userData.getNeedsPickUp() ? "1" : "0");
			requestJSON.put("seats", "0");
		} catch (JSONException e) {
			DebugHelper.ShowMessage.t(this,
					"An error occured processing the response");
		}

		if (NetworkHelper.Utilities.isConnected(this)) {
			UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this);
			try {
				progDialog = ProgressDialog.show(this, "Processing...",
						"Fetching data", true, false);
				udwTask.execute("POST", serverURL + "users/reservedates",
						requestJSON.toString());

			} catch (Exception e) {
				if (progDialog.isShowing()) {
					progDialog.dismiss();
				}
				udwTask.cancel(true);
			}
		} else {
			DebugHelper.ShowMessage.t(this, "Connection error");
		}

		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);

		Intent intent = null;

		// if (userData.getNeedsAccommodation() == true
		// && userData.getNeedsPickUp() == false) {
		// intent = new Intent(this, AccomodationActivity.class);
		// intent.putExtra("hasPickup", false);
		// } else if (userData.getNeedsAccommodation() == false
		// && userData.getNeedsPickUp() == true) {
		// intent = new Intent(this, PickupActivity.class);
		// } else if (userData.getNeedsAccommodation() == true
		// && userData.getNeedsPickUp() == true) {
		// intent = new Intent(this, AccomodationActivity.class);
		// intent.putExtra("hasPickup", true);
		// } else {
		// intent = new Intent(this, TalksActivity.class);
		// }

		if (userData.getNeedsAccommodation() == true) {
			if (intent == null) {
				intent = new Intent(this, AccomodationActivity.class);
			}
		}

		if (userData.getNeedsPickUp() == true) {
			if (intent == null) {
				intent = new Intent(this, PickupActivity.class);
			}
			intent.putExtra("hasPickup", true);
		}

		if (userData.getHasTalk() == true) {
			if (intent == null) {
				intent = new Intent(this, TalksActivity.class);
			}
			intent.putExtra("hasTalk", true);
		}

		if (intent == null) {
			intent = new Intent(this, SummaryActivity.class);
		}

		startActivity(intent);
	}

	private void initializeLayout() {
		int daysCount = Days.daysBetween(
				new DateTime(startDateString).toLocalDate(),
				new DateTime(endDateString).toLocalDate()).getDays() + 1;
		needsAccommodation = userData.getNeedsAccommodation();
		needsPickup = userData.getNeedsPickUp();
		hasTalk = userData.getHasTalk();

		CheckBox accommodationCheck = (CheckBox) this
				.findViewById(R.id.accommodationCheck);
		CheckBox pickupCheck = (CheckBox) findViewById(R.id.pickupCheck);
		CheckBox talkCheck = (CheckBox) findViewById(R.id.talkCheck);

		if (needsAccommodation == true) {
			accommodationCheck.setChecked(true);
		}

		if (needsPickup == true) {
			pickupCheck.setChecked(true);
		}

		if (hasTalk == true) {
			talkCheck.setChecked(true);
		}

		if (userData.getEventDaysAttending() != null) {
			eventDays = userData.getEventDaysAttending();
		} else {
			eventDays = new Boolean[daysCount];
			java.util.Arrays.fill(eventDays, false);
			userData.setEventDaysAttending(eventDays);
			StorageHelper.PreferencesHelper.setUserData(this, identifier,
					userData);
		}
	}

	@Override
	public void postAsyncTaskCallback(String responseBody, String responseCode) {
		DateTime startDate = new DateTime(startDateString);
		int currentDay = 0;

		JSONObject responseJSON;

		if (progDialog.isShowing()) {
			progDialog.dismiss();
		}

		if (responseBody.length() == 0) {
			DebugHelper.ShowMessage
					.t(this,
							"There was an error processing your request. Please try again later.");
		} else {
			try {
				responseJSON = new JSONObject(responseBody);

				if (responseJSON.has("user") == true) {
					// DebugHelper.ShowMessage.d(responseJSON.getString("dates")
					// .toString());
					JSONArray dates = new JSONArray(
							responseJSON.getString("dates"));
					for (int i = 0; i < dates.length(); i++) {
						DateTime date = new DateTime(dates.get(i));
						currentDay = Days.daysBetween(startDate.toLocalDate(),
								date.toLocalDate()).getDays();
						eventDays[currentDay] = true;
					}

					needsAccommodation = responseJSON
							.getBoolean("accommodation");
					needsPickup = responseJSON.getBoolean("pickup");
					// DebugHelper.ShowMessage.d(responseJSON.getString("dates")
					// .toString());
				}
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured processing the response");
			}
		}
	}

	private void getEventDaysUpdate() {
		String token = userData.getAuthToken();

		if (NetworkHelper.Utilities.isConnected(this)) {
			UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this);
			try {
				progDialog = ProgressDialog.show(this, "Processing...",
						"Fetching data", true, false);
				udwTask.execute("GET", serverURL + "users/reservedates?user="
						+ URLEncoder.encode(identifier, "UTF-8") + "&hhtoken="
						+ token);

			} catch (Exception e) {
				if (progDialog.isShowing()) {
					progDialog.dismiss();
				}
				udwTask.cancel(true);
			}
		} else {
			DebugHelper.ShowMessage.t(this, "Connection error");
		}
	}

	private void initializeListeners() {
		accommodationCheck.setOnCheckedChangeListener(this);
		pickupCheck.setOnCheckedChangeListener(this);
		talkCheck.setOnCheckedChangeListener(this);

		eventCalendarGrid.setOnItemClickListener(this);
	}

	private void initalizeAdapter() {
		eventCalendarAdapter = new EventCalendarAdapter(this, startDateString,
				endDateString, eventDays);

		eventCalendarGrid.setAdapter(eventCalendarAdapter);
	}
}
