package foo.fruitfox.evend;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.UserDataWebAPITask;
import foo.fruitfox.tasks.UserDataWebAPITask.AsyncResponseListener;

public class WelcomeActivity extends ActionBarActivity implements
		OnCheckedChangeListener, OnClickListener, OnFocusChangeListener,
		AsyncResponseListener, android.content.DialogInterface.OnClickListener,
		android.content.DialogInterface.OnCancelListener {

	private EditText attendanceStartDate;
	private EditText attendanceEndDate;
	private EditText attendanceName;
	private CheckBox accommodationCheck;
	private CheckBox pickupCheck;
	private CheckBox talkCheck;

	private Context context;

	private Boolean needsAccommodation = false;
	private Boolean needsPickup = false;
	private Boolean hasTalk = false;
	private String userName;

	private AlertDialog.Builder pickupAlertDialogBuilder;

	private String identifier;
	private UserData userData;

	private String startDateString = "07-05-2015";
	private String endDateString = "07-06-2015";
	private String currentStartDateString = "07-05-2015";
	private String currentEndDateString = "07-06-2015";

	private String serverURL;

	private ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		context = this;

		accommodationCheck = (CheckBox) findViewById(R.id.accommodationCheck);
		pickupCheck = (CheckBox) findViewById(R.id.pickupCheck);
		talkCheck = (CheckBox) findViewById(R.id.talkCheck);

		attendanceName = (EditText) findViewById(R.id.attendanceName);
		attendanceStartDate = (EditText) findViewById(R.id.attendanceStartDate);
		attendanceEndDate = (EditText) findViewById(R.id.attendanceEndDate);

		pickupAlertDialogBuilder = new AlertDialog.Builder(context);

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		serverURL = getResources().getString(R.string.server_url);

		initializeLayout();

		initializeListeners();

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
		if (id == R.id.action_about) {
			DebugHelper.ShowMessage.showAbout(this);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.accommodationCheck:
			needsAccommodation = isChecked;
			break;

		case R.id.pickupCheck:
			if (isChecked) {
				pickupAlertDialogBuilder.show();
			}

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
		JSONObject requestJSON = new JSONObject();

		DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");

		userName = attendanceName.getText().toString().trim();

		if (userName.length() > 0) {
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

			userData.setAttendanceStartDate("dd-MM-yyyy", attendanceStartDate
					.getText().toString());

			userData.setAttendanceEndDate("dd-MM-yyyy", attendanceEndDate
					.getText().toString());

			userData.setEventDaysAttending(dtf.parseDateTime(startDateString),
					dtf.parseDateTime(endDateString));

			userData.setFirstName(userName);

			try {
				requestJSON.put("hhtoken", userData.getAuthToken());
				requestJSON.put("username", userData.getFirstName());
				requestJSON.put("arrival_date",
						userData.getAttendanceStartDate("dd-MM-yyyy"));
				requestJSON.put("departure_date",
						userData.getAttendanceEndDate("dd-MM-yyyy"));
				requestJSON.put("accommodation",
						userData.getNeedsAccommodation() ? 1 : 0);
				requestJSON.put("pickup", userData.getNeedsPickUp() ? 1 : 0);
				requestJSON.put("accommodation",
						userData.getNeedsAccommodation() ? 1 : 0);
				requestJSON.put("talk", userData.getHasTalk() ? 1 : 0);
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured while creating the JSON request.");
				DebugHelper.ShowMessage.d("WelcomeActivity", e.getMessage());
			}

			if (NetworkHelper.Utilities.isConnected(this)) {
				UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this);
				try {
					progDialog = ProgressDialog.show(this, "Please wait...",
							"Saving your data", true, false);
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

			StorageHelper.PreferencesHelper.setUserData(this, identifier,
					userData);

			Intent intent = null;

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
		} else {
			DebugHelper.ShowMessage.t(this, "Your name cannot be blank");
		}
	}

	private void initializeLayout() {
		String pickupWarningText = getResources().getString(
				R.string.welcome_pickup_warning_text);
		needsAccommodation = userData.getNeedsAccommodation();
		needsPickup = userData.getNeedsPickUp();
		hasTalk = userData.getHasTalk();

		if (userData.getAttendanceStartDate("dd-MM-yyyy").length() > 0) {
			currentStartDateString = userData
					.getAttendanceStartDate("dd-MM-yyyy");
		}

		if (userData.getAttendanceEndDate("dd-MM-yyyy").length() > 0) {
			currentEndDateString = userData.getAttendanceEndDate("dd-MM-yyyy");
		}

		pickupAlertDialogBuilder.setMessage(pickupWarningText);

		if (needsAccommodation == true) {
			accommodationCheck.setChecked(true);
		}

		if (needsPickup == true) {
			pickupCheck.setChecked(true);
		}

		if (hasTalk == true) {
			talkCheck.setChecked(true);
		}

		if (currentStartDateString.length() > 0) {
			attendanceStartDate.setText(currentStartDateString);
		} else {
			attendanceStartDate.setText(startDateString);
		}

		if (currentEndDateString.length() > 0) {
			attendanceEndDate.setText(currentEndDateString);
		} else {
			attendanceEndDate.setText(endDateString);
		}

		if (userData.getFirstName() != null
				&& userData.getFirstName().length() > 0) {
			attendanceName.setText(userData.getFirstName().toString());
		}
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
			DebugHelper.ShowMessage.d("WelcomeActivity", "Response Code : "
					+ responseCode);
			DebugHelper.ShowMessage.d("WelcomeActivity", "Response Body : "
					+ responseBody);
		} else {
			try {
				responseJSON = new JSONObject(responseBody);

				if (responseJSON.has("error") == true) {
					DebugHelper.ShowMessage.t(this,
							responseJSON.getString("error"));
					DebugHelper.ShowMessage.d("WelcomeActivity",
							"Response Code :" + responseCode);
					DebugHelper.ShowMessage.d("WelcomeActivity",
							"Response Body :" + responseBody);
				}
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured trying to parse the JSON response");
				DebugHelper.ShowMessage.d("WelcomeActivity", "Response Code : "
						+ responseCode);
				DebugHelper.ShowMessage.d("WelcomeActivity", "Response Body : "
						+ responseBody);
				DebugHelper.ShowMessage.d("WelcomeActivity", e.getMessage());
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
				DebugHelper.ShowMessage
						.t(this,
								"An error occurred while processing your request. Please try again later.");
				DebugHelper.ShowMessage.d("WelcomeActivity", e.getMessage());
			}
		} else {
			DebugHelper.ShowMessage.t(this, "Unable to connect to the server.");
		}
	}

	private void initializeListeners() {
		accommodationCheck.setOnCheckedChangeListener(this);
		pickupCheck.setOnCheckedChangeListener(this);
		talkCheck.setOnCheckedChangeListener(this);

		attendanceStartDate.setOnClickListener(this);
		attendanceStartDate.setOnFocusChangeListener(this);

		attendanceEndDate.setOnClickListener(this);
		attendanceEndDate.setOnFocusChangeListener(this);

		pickupAlertDialogBuilder.setPositiveButton("Yes", this);
		pickupAlertDialogBuilder.setNegativeButton("No", this);
		pickupAlertDialogBuilder.setOnCancelListener(this);
	}

	private void displayStartDateCalendar() {
		final Dialog dialog = new Dialog(context);

		DateTime startDate = new DateTime("2015-05-07");
		DateTime endDate = new DateTime("2015-06-07");

		dialog.setTitle("Select your attending date");
		dialog.setContentView(R.layout.calendar_date_picker);

		final Button acceptDate = (Button) dialog.findViewById(R.id.acceptDate);

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

		DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
		DateTime currentSelectedDate = dtf
				.parseDateTime(currentStartDateString);
		calendar.setDate(currentSelectedDate.getMillis());

		// sets the listener to be notified upon selected date change.
		calendar.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				LinearLayout attendanceStartDateLayout = (LinearLayout) findViewById(R.id.attendanceStartDateContainer);
				EditText attendanceStartDate = (EditText) attendanceStartDateLayout
						.findViewById(R.id.attendanceStartDate);

				String startDateString = dayOfMonth + "-" + (month + 1) + "-"
						+ year;

				DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
				long epochDifference = dtf.parseDateTime(startDateString)
						.getMillis()
						- dtf.parseDateTime(currentEndDateString).getMillis();

				if (epochDifference <= 0) {
					attendanceStartDate.setText(startDateString);
					currentStartDateString = startDateString;
				} else {
					DebugHelper.ShowMessage.t(context,
							"Your start date cannot be after the end date");
				}
			}
		});

		acceptDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void displayEndDateCalendar() {
		final Dialog dialog = new Dialog(context);

		DateTime startDate = new DateTime("2015-05-07");
		DateTime endDate = new DateTime("2015-06-07");

		dialog.setTitle("Select your leaving date");
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

		DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
		DateTime currentSelectedDate = dtf.parseDateTime(currentEndDateString);
		calendar.setDate(currentSelectedDate.getMillis());

		// sets the listener to be notified upon selected date change.
		calendar.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				LinearLayout attendanceEndDateLayout = (LinearLayout) findViewById(R.id.attendanceEndDateContainer);
				EditText attendanceEndDate = (EditText) attendanceEndDateLayout
						.findViewById(R.id.attendanceEndDate);

				String endDateString = dayOfMonth + "-" + (month + 1) + "-"
						+ year;

				DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
				long epochDifference = dtf
						.parseDateTime(currentStartDateString).getMillis()
						- dtf.parseDateTime(endDateString).getMillis();

				if (epochDifference <= 0) {
					attendanceEndDate.setText(endDateString);
					currentEndDateString = endDateString;
				} else {
					DebugHelper.ShowMessage.t(context,
							"Your end date cannot be before the start date");
				}
			}
		});

		acceptDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (view.getId() == R.id.attendanceStartDate && hasFocus) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayStartDateCalendar();
		}

		if (view.getId() == R.id.attendanceEndDate && hasFocus) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayEndDateCalendar();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.attendanceStartDate) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayStartDateCalendar();
		}

		if (view.getId() == R.id.attendanceEndDate) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayEndDateCalendar();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			needsPickup = true;
			pickupCheck.setChecked(true);
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			needsPickup = false;
			pickupCheck.setChecked(false);
			break;
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		pickupCheck.setChecked(false);
	}
}
