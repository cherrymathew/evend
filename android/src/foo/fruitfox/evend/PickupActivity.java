package foo.fruitfox.evend;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import foo.fruitfox.data.PickupData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.UserDataWebAPITask;
import foo.fruitfox.tasks.UserDataWebAPITask.AsyncResponseListener;

public class PickupActivity extends ActionBarActivity implements
		OnItemSelectedListener, OnClickListener, OnFocusChangeListener,
		AsyncResponseListener {
	private List<String> locationList;
	private List<String> seatsCountList;

	private ArrayAdapter<String> locationAdapter;
	private ArrayAdapter<String> seatsCountAdapter;

	private String[] locationArray;

	private Spinner location;
	private Spinner seatsCount;

	private Context context;

	private ProgressDialog progDialog;

	private Boolean hasTalk;

	private String identifier;
	private UserData userData;
	private PickupData pickupData;

	private String serverURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pickup);

		location = (Spinner) findViewById(R.id.location);
		seatsCount = (Spinner) findViewById(R.id.seatsCount);

		location.setFocusable(true);
		location.setFocusableInTouchMode(true);
		location.requestFocus();

		context = this;

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		serverURL = getResources().getString(R.string.server_url);

		Intent intent = getIntent();
		hasTalk = intent.getBooleanExtra("hasTalk", false);

		initializeAdapters();
		initializeListeners();
	}

	protected void onResume() {
		super.onResume();

		int position = 0;

		EditText pickupDate = (EditText) findViewById(R.id.pickupDate);
		EditText pickupTime = (EditText) findViewById(R.id.pickupTime);
		String timeString = "";
		String dateString = "";

		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		if (userData.getPickupData() != null) {
			pickupData = userData.getPickupData();

			position = locationAdapter.getPosition(pickupData.getLocation());
			location.setSelection(position);

			position = seatsCountAdapter
					.getPosition(pickupData.getSeatsCount());
			seatsCount.setSelection(position);

			dateString = pickupData.getPickupDate("dd-MM-yyyy");
			if (dateString.length() > 0) {
				pickupDate.setText(dateString);
			}

			timeString = pickupData.getPickupTime("HH:mm");
			if (timeString.length() > 0) {
				pickupTime.setText(timeString);
			}

		} else {
			pickupData = new PickupData();

			if (userData.getNeedsAccommodation()) {
				position = seatsCountAdapter.getPosition(userData
						.getAccommodationData().getBedsCount());
				seatsCount.setSelection(position);

				dateString = userData.getAttendanceStartDate("dd-MM-yyyy");
				if (dateString.length() > 0) {
					pickupDate.setText(dateString);
				}

				timeString = DateTime.now().toString("HH") + ":00";
				if (timeString.length() > 0) {
					pickupTime.setText(timeString);
				}
			}
		}

	}

	protected void onPause() {
		super.onPause();

		EditText pickupDate = (EditText) findViewById(R.id.pickupDate);
		EditText pickupTime = (EditText) findViewById(R.id.pickupTime);

		pickupData.setPickupDate("dd-MM-yyyy", pickupDate.getText().toString());
		pickupData.setPickupTime("HH:mm", pickupTime.getText().toString());

		userData.setPickupData(pickupData);

		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pickup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {
			DebugHelper.ShowMessage.showAbout(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initializeAdapters() {
		int seatsMaxCount = Integer.parseInt(getResources().getString(
				R.string.pickup_seats_count).trim()) + 1;

		locationList = new ArrayList<String>();

		locationArray = getResources().getStringArray(R.array.location_names);

		for (int i = 0; i < locationArray.length; i++) {
			locationList.add(locationArray[i]);
		}

		seatsCountList = new ArrayList<String>();

		for (int i = 1; i < seatsMaxCount; i++) {
			seatsCountList.add("" + i);
		}

		locationAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, locationList);

		locationAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

		seatsCountAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, seatsCountList);

		seatsCountAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

		location.setAdapter(locationAdapter);
		seatsCount.setAdapter(seatsCountAdapter);
	}

	private void initializeListeners() {
		EditText pickupDate = (EditText) findViewById(R.id.pickupDate);
		EditText pickupTime = (EditText) findViewById(R.id.pickupTime);

		seatsCount.setOnItemSelectedListener(this);
		location.setOnItemSelectedListener(this);
		pickupDate.setOnClickListener(this);
		pickupDate.setOnFocusChangeListener(this);
		pickupTime.setOnClickListener(this);
		pickupTime.setOnFocusChangeListener(this);
	}

	private void displayCalendar() {
		final Dialog dialog = new Dialog(context);
		EditText pickupDate = (EditText) findViewById(R.id.pickupDate);

		DateTime startDate = new DateTime("2015-05-01");
		DateTime endDate = new DateTime("2015-06-07");

		dialog.setTitle("Select your arrival date");
		dialog.setContentView(R.layout.calendar_date_picker);

		Button acceptDate = (Button) dialog.findViewById(R.id.acceptDate);

		CalendarView calendar = (CalendarView) dialog
				.findViewById(R.id.eventCalendarView);

		// sets whether to show the week number.
		calendar.setShowWeekNumber(false);

		calendar.setMinDate(startDate.getMillis());
		calendar.setMaxDate(endDate.getMillis());

		calendar.setMinimumHeight(50);

		// sets the first day of week according to Calendar.
		// here we set Monday as the first day of the Calendar
		calendar.setFirstDayOfWeek(Calendar.MONDAY);

		// The background color for the selected week.
		calendar.setSelectedWeekBackgroundColor(getResources().getColor(
				R.color.aqua));

		// sets the color for the vertical bar shown at the beginning and at
		// the end of the selected date.
		calendar.setSelectedDateVerticalBar(R.color.silver);

		if (pickupDate.getText().toString().length() > 0) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
			DateTime currentSelectedDate = dtf.parseDateTime(pickupDate
					.getText().toString());
			calendar.setDate(currentSelectedDate.getMillis());
		}

		// sets the listener to be notified upon selected date change.
		calendar.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				LinearLayout pickupDateLayout = (LinearLayout) findViewById(R.id.pickupDateContainer);
				EditText pickupDate = (EditText) pickupDateLayout
						.findViewById(R.id.pickupDate);
				pickupDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
				dialog.dismiss();
			}
		});

		acceptDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				LinearLayout pickupDateLayout = (LinearLayout) findViewById(R.id.pickupDateContainer);
				EditText pickupDate = (EditText) pickupDateLayout
						.findViewById(R.id.pickupDate);

				if (pickupDate.getText().toString().length() == 0) {
					pickupDate.setText("23-05-2015");
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void displayTimePicker() {
		final Dialog dialog = new Dialog(context);
		final String[] DISPLAYED_MINS = { "00", "15", "30", "45" };

		dialog.setTitle("Select your arrival time");
		dialog.setContentView(R.layout.time_picker);

		Button acceptTime = (Button) dialog.findViewById(R.id.acceptTime);
		final TimePicker pickupTimePicker = (TimePicker) dialog
				.findViewById(R.id.pickupTimePicker);

		NumberPicker initMinuteSpinner = getMinuteSpinner(pickupTimePicker,
				"minute");
		NumberPicker initHourSpinner = getMinuteSpinner(pickupTimePicker,
				"hour");

		if (initMinuteSpinner != null) {
			initMinuteSpinner.setMinValue(0);
			initMinuteSpinner.setMaxValue(DISPLAYED_MINS.length - 1);
			initMinuteSpinner.setDisplayedValues(DISPLAYED_MINS);
			initMinuteSpinner.setWrapSelectorWheel(true);
			initMinuteSpinner
					.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		}

		if (initHourSpinner != null) {
			initHourSpinner
					.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		}

		pickupTimePicker.setIs24HourView(true);

		pickupTimePicker.setCurrentMinute(0);

		pickupTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				NumberPicker minuteSpinner = getMinuteSpinner(view, "minute");

				if (minuteSpinner != null) {
					minuteSpinner.setMinValue(0);
					minuteSpinner.setMaxValue(DISPLAYED_MINS.length - 1);
					minuteSpinner.setDisplayedValues(DISPLAYED_MINS);
					minuteSpinner.setWrapSelectorWheel(true);
					minuteSpinner
							.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
				}
			}
		});

		acceptTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				String timeString = "";

				if (pickupTimePicker.getCurrentHour().intValue() < 10) {
					timeString = "0"
							+ pickupTimePicker.getCurrentHour()
							+ ":"
							+ DISPLAYED_MINS[pickupTimePicker
									.getCurrentMinute()];
				} else {
					timeString = pickupTimePicker.getCurrentHour()
							+ ":"
							+ DISPLAYED_MINS[pickupTimePicker
									.getCurrentMinute()];
				}

				EditText pickupTime = (EditText) findViewById(R.id.pickupTime);
				pickupTime.setText(timeString);
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private NumberPicker getMinuteSpinner(TimePicker view, String type) {
		Field field = null;
		NumberPicker minuteSpiner = null;

		try {
			if (type == "minute") {
				field = view.getClass().getDeclaredField("mMinuteSpinner");
			} else if (type == "hour") {
				field = view.getClass().getDeclaredField("mHourSpinner");
			}
			field.setAccessible(true);
			minuteSpiner = (NumberPicker) field.get(view);

		} catch (NoSuchFieldException e) {
			DebugHelper.ShowMessage.d("Minute Field not found");
		} catch (IllegalAccessException e) {
			DebugHelper.ShowMessage.d("Minute Field cannot be accessed");
		}

		return minuteSpiner;
	}

	public void next(View view) {
		Intent intent = null;
		JSONObject requestJSON = new JSONObject();

		if (hasTalk == true) {
			if (intent == null) {
				intent = new Intent(this, TalksActivity.class);
			}
			intent.putExtra("hasTalk", true);
		}

		if (intent == null) {
			intent = new Intent(this, SummaryActivity.class);
		}

		EditText pickupDate = (EditText) findViewById(R.id.pickupDate);
		EditText pickupTime = (EditText) findViewById(R.id.pickupTime);

		pickupData.setPickupDate("dd-MM-yyyy", pickupDate.getText().toString());
		pickupData.setPickupTime("HH:mm", pickupTime.getText().toString());

		userData.setPickupData(pickupData);

		try {
			requestJSON.put("hhtoken", userData.getAuthToken());
			requestJSON.put("date",
					userData.getPickupData().getPickupDate("dd-MM-yyyy"));
			requestJSON.put("location", userData.getPickupData().getLocation());
			requestJSON.put("seats", userData.getPickupData().getSeatsCount());
			requestJSON.put("time",
					userData.getPickupData().getPickupTime("HH:mm"));
		} catch (JSONException e) {
			DebugHelper.ShowMessage.t(this,
					"An error occured while creating the JSON request.");
			DebugHelper.ShowMessage.d("PickupActivity", e.getMessage());
		}

		if (NetworkHelper.Utilities.isConnected(this)) {
			UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this);
			try {
				progDialog = ProgressDialog.show(this, "Please wait...",
						"Saving your data", true, false);
				udwTask.execute("POST", serverURL + "users/pickup",
						requestJSON.toString());

			} catch (Exception e) {
				if (progDialog.isShowing()) {
					progDialog.dismiss();
				}
				udwTask.cancel(true);
				DebugHelper.ShowMessage
						.t(this,
								"An error occurred while processing your request. Please try again later.");
				DebugHelper.ShowMessage.d("PickupActivity", e.getMessage());
			}
		} else {
			DebugHelper.ShowMessage.t(this, "Unable to connect to the server.");
		}

		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);

		startActivity(intent);
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (view.getId() == R.id.pickupDate && hasFocus) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayCalendar();
		}

		if (view.getId() == R.id.pickupTime && hasFocus) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayTimePicker();
		}

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.pickupDate) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayCalendar();
		}

		if (view.getId() == R.id.pickupTime) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayTimePicker();
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == R.id.seatsCount) {
			pickupData.setSeatsCount(parent.getItemAtPosition(position)
					.toString());
		} else if (parent.getId() == R.id.location) {
			pickupData.setLocation(parent.getItemAtPosition(position)
					.toString());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void postAsyncTaskCallback(String responseBody, String responseCode) {
		JSONObject responseJSON;

		if (progDialog.isShowing()) {
			progDialog.dismiss();
		}

		if (responseBody.length() == 0) {
			DebugHelper.ShowMessage
					.t(this,
							"There was an error processing your request. Please try again later.");
			DebugHelper.ShowMessage.d("PickupActivity", "Response Code : "
					+ responseCode);
			DebugHelper.ShowMessage.d("PickupActivity", "Response Body : "
					+ responseBody);
		} else {
			try {
				responseJSON = new JSONObject(responseBody);

				if (responseJSON.has("error") == true) {
					DebugHelper.ShowMessage.t(this,
							responseJSON.getString("error"));
					DebugHelper.ShowMessage.d("PickupActivity",
							"Response Code :" + responseCode);
					DebugHelper.ShowMessage.d("PickupActivity",
							"Response Body :" + responseBody);
				}
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured trying to parse the JSON response");
				DebugHelper.ShowMessage.d("PickupActivity", "Response Code : "
						+ responseCode);
				DebugHelper.ShowMessage.d("PickupActivity", "Response Body : "
						+ responseBody);
				DebugHelper.ShowMessage.d("PickupActivity", e.getMessage());
			}
		}

	}
}
