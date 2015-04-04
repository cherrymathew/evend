package foo.fruitfox.evend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Dialog;
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
import android.widget.Spinner;
import foo.fruitfox.data.AccommodationData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.StorageHelper;

public class AccomodationActivity extends ActionBarActivity implements
		OnItemSelectedListener, OnClickListener, OnFocusChangeListener {
	private List<String> accommodationTypesList;
	private List<String> bedsCountList;
	private List<String> daysCountList;

	private ArrayAdapter<String> accommodationTypesAdapter;
	private ArrayAdapter<String> bedsCountAdapter;
	private ArrayAdapter<String> daysCountAdapter;

	private Spinner accommodationTypes;
	private Spinner bedsCount;
	private Spinner daysCount;

	private String identifier;
	private UserData userData;
	private AccommodationData accommodationData;

	private boolean hasPickup;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accomodation);

		context = this;

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		accommodationTypes = (Spinner) findViewById(R.id.accommodationTypes);
		bedsCount = (Spinner) findViewById(R.id.bedsCount);
		daysCount = (Spinner) findViewById(R.id.daysCount);

		accommodationTypes.setFocusable(true);
		accommodationTypes.setFocusableInTouchMode(true);
		accommodationTypes.requestFocus();

		Intent intent = getIntent();
		hasPickup = intent.getBooleanExtra("hasPickup", false);

		initializeAdapters();
		initializeListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accomodation, menu);
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
		int position = 0;

		EditText accommodationStartDate = (EditText) findViewById(R.id.accommodationStartDate);
		String dateString = "";

		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		if (userData.getAccommodationData() != null) {
			accommodationData = userData.getAccommodationData();

			position = accommodationTypesAdapter.getPosition(accommodationData
					.getAccommodationType());
			accommodationTypes.setSelection(position);

			position = bedsCountAdapter.getPosition(accommodationData
					.getBedsCount());
			bedsCount.setSelection(position);

			position = daysCountAdapter.getPosition(accommodationData
					.getDaysCount());
			daysCount.setSelection(position);

			dateString = accommodationData.getStartDate("dd-MM-yyyy");
			if (dateString.length() > 0) {
				accommodationStartDate.setText(dateString);
			}

		} else {
			accommodationData = new AccommodationData();
		}
	}

	protected void onPause() {
		super.onPause();

		EditText accommodationStartDate = (EditText) findViewById(R.id.accommodationStartDate);

		accommodationData.setStartDate("dd-MM-yyyy", accommodationStartDate
				.getText().toString());

		userData.setAccommodationData(accommodationData);

		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	private void initializeAdapters() {
		accommodationTypesList = new ArrayList<String>();
		bedsCountList = new ArrayList<String>();
		daysCountList = new ArrayList<String>();

		accommodationTypesList.add("Room");
		accommodationTypesList.add("Bed");
		accommodationTypesList.add("Tent");

		for (int i = 1; i < 6; i++) {
			bedsCountList.add("" + i);
		}

		for (int i = 1; i < 15; i++) {
			daysCountList.add("" + i);
		}

		accommodationTypesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, accommodationTypesList);

		accommodationTypesAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

		bedsCountAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, bedsCountList);

		bedsCountAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

		daysCountAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, daysCountList);

		daysCountAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

		accommodationTypes.setAdapter(accommodationTypesAdapter);
		bedsCount.setAdapter(bedsCountAdapter);
		daysCount.setAdapter(daysCountAdapter);
	}

	private void initializeListeners() {
		EditText accommodationStartDate = (EditText) findViewById(R.id.accommodationStartDate);

		accommodationTypes.setOnItemSelectedListener(this);
		bedsCount.setOnItemSelectedListener(this);
		daysCount.setOnItemSelectedListener(this);
		accommodationStartDate.setOnClickListener(this);
		accommodationStartDate.setOnFocusChangeListener(this);
	}

	private void displayCalendar() {
		final Dialog dialog = new Dialog(context);
		EditText accommodationStartDate = (EditText) findViewById(R.id.accommodationStartDate);

		DateTime startDate = new DateTime("2015-05-23");
		DateTime endDate = new DateTime("2015-06-07");

		dialog.setTitle("Select your date");
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

		if (accommodationStartDate.getText().toString().length() > 0) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
			DateTime currentSelectedDate = dtf
					.parseDateTime(accommodationStartDate.getText().toString());
			calendar.setDate(currentSelectedDate.getMillis());
		}

		// sets the listener to be notified upon selected date change.
		calendar.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				LinearLayout accommodationStartDateLayout = (LinearLayout) findViewById(R.id.accommodationStartDateContainer);
				EditText accommodationStartDate = (EditText) accommodationStartDateLayout
						.findViewById(R.id.accommodationStartDate);
				accommodationStartDate.setText(dayOfMonth + "-" + (month + 1)
						+ "-" + year);
				dialog.dismiss();
			}
		});

		acceptDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				LinearLayout accommodationStartDateLayout = (LinearLayout) findViewById(R.id.accommodationStartDateContainer);
				EditText accommodationStartDate = (EditText) accommodationStartDateLayout
						.findViewById(R.id.accommodationStartDate);

				if (accommodationStartDate.getText().toString().length() == 0) {
					accommodationStartDate.setText("23-05-2015");
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void next(View view) {
		Intent intent;

		if (hasPickup == true) {
			intent = new Intent(this, PickupActivity.class);
		} else {
			intent = new Intent(this, TalksActivity.class);
		}

		EditText accommodationStartDate = (EditText) findViewById(R.id.accommodationStartDate);

		accommodationData.setStartDate("dd-MM-yyyy", accommodationStartDate
				.getText().toString());

		userData.setAccommodationData(accommodationData);

		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);

		startActivity(intent);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		if (parent.getId() == R.id.accommodationTypes) {
			accommodationData.setAccommodationType(parent.getItemAtPosition(
					position).toString());
		} else if (parent.getId() == R.id.bedsCount) {
			accommodationData.setBedsCount(parent.getItemAtPosition(position)
					.toString());
		} else if (parent.getId() == R.id.daysCount) {
			accommodationData.setDaysCount(parent.getItemAtPosition(position)
					.toString());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (view.getId() == R.id.accommodationStartDate && hasFocus) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayCalendar();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.accommodationStartDate) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayCalendar();
		}
	}
}
