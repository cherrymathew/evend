package foo.fruitfox.evend;

import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import foo.fruitfox.adapters.TalksAdapter;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.UserDataWebAPITask;
import foo.fruitfox.tasks.UserDataWebAPITask.AsyncResponseListener;

public class TalksActivity extends ActionBarActivity implements
		OnFocusChangeListener, OnClickListener, OnItemClickListener,
		AsyncResponseListener {

	private List<TalkData> talksList;
	private ListView talksListView;
	private TalksAdapter ta;

	private Context context;

	private UserData userData;
	private String identifier;

	private String serverURL;

	private ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talks);

		this.context = this;
		this.identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		this.userData = StorageHelper.PreferencesHelper.getUserData(this,
				identifier);

		serverURL = getResources().getString(R.string.server_url);

		initalizeAdapter();
		initializeListeners();
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
	}

	public void onResume() {
		super.onResume();
		ta.notifyDataSetChanged();
	}

	public void talkRemove(View view) {
		int position = talksListView
				.getPositionForView((View) view.getParent());
		talksList.remove(position);
		ta.notifyDataSetChanged();
		userData.setTalkDataList(talksList);
		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	public void addTalk(View view) {
		LinearLayout addTalkLayout = (LinearLayout) findViewById(R.id.addTalkLayout);
		EditText talkTitle = (EditText) addTalkLayout
				.findViewById(R.id.talkTitleEdit);
		EditText talkDate = (EditText) addTalkLayout
				.findViewById(R.id.talkDateEdit);

		String talkTitleText = talkTitle.getText().toString();
		String talkDateText = talkDate.getText().toString();

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

		if (talkTitle.getTag() == null) {
			if (talkTitleText.length() > 0 && talkDateText.length() > 0) {
				String date = talkDateText.split("-")[2] + "-"
						+ talkDateText.split("-")[1] + "-"
						+ talkDateText.split("-")[0];

				TalkData talk = new TalkData(talkTitleText, date);

				talksList.add(talk);
				ta.notifyDataSetChanged();

				talkTitle.setText("");
				talkDate.setText("");
			} else if (talkTitleText.length() > 0) {
				TalkData talk = new TalkData(talkTitleText);

				talksList.add(talk);
				ta.notifyDataSetChanged();

				talkTitle.setText("");
				talkDate.setText("");
			}
		} else {
			int position = (int) talkTitle.getTag();
			talkTitle.setTag(null);

			if (talkTitleText.length() > 0 && talkDateText.length() > 0) {
				talksList.get(position).setTitle(talkTitleText);
				talksList.get(position).setDate("dd-MM-yyyy", talkDateText);

				ta.notifyDataSetChanged();

				talkTitle.setText("");
				talkDate.setText("");
			}
		}

		userData.setTalkDataList(talksList);
		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	public void displayCalendar() {
		final Dialog dialog = new Dialog(context);

		DateTime startDate = new DateTime("2015-05-23");
		DateTime endDate = new DateTime("2015-06-07");

		dialog.setContentView(R.layout.calendar_date_picker);

		Button acceptDate = (Button) dialog.findViewById(R.id.acceptDate);

		CalendarView calendar = (CalendarView) dialog
				.findViewById(R.id.calendarView1);

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

		// sets the listener to be notified upon selected date change.
		calendar.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				LinearLayout addTalkLayout = (LinearLayout) findViewById(R.id.addTalkLayout);
				EditText talkDate = (EditText) addTalkLayout
						.findViewById(R.id.talkDateEdit);
				talkDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
				dialog.dismiss();
			}
		});

		acceptDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				LinearLayout addTalkLayout = (LinearLayout) findViewById(R.id.addTalkLayout);
				EditText talkDate = (EditText) addTalkLayout
						.findViewById(R.id.talkDateEdit);

				if (talkDate.getText().toString().length() == 0) {
					talkDate.setText("23-05-2015");
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (view.getId() == R.id.talkDateEdit && hasFocus) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayCalendar();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.talkDateEdit) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			displayCalendar();
		}
	}

	public void next(View view) {
		JSONObject requestJSON = new JSONObject();
		JSONArray talksJSONArray = new JSONArray();

		try {
			requestJSON.put("hhtoken", userData.getAuthToken());
			if (talksList.size() > 0) {
				for (TalkData talk : talksList) {
					JSONObject talksJSON = new JSONObject();
					talksJSON.put("title", talk.getTitle());
					talksJSON.put("datetime", talk.getDate("yyyy-MM-dd"));
					talksJSONArray.put(talksJSON);
				}
			}

			requestJSON.put("talks", talksJSONArray);

			if (NetworkHelper.Utilities.isConnected(this)) {
				UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this);
				try {
					progDialog = ProgressDialog.show(this, "Processing...",
							"Fetching data", true, false);
					udwTask.execute("POST", serverURL + "users/talk/add",
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
		} catch (JSONException e) {
			DebugHelper.ShowMessage.t(this,
					"An error occured processing the response");
		}
		DebugHelper.ShowMessage.t(this, "next");
	}

	private void initializeListeners() {
		LinearLayout addTalkLayout = (LinearLayout) findViewById(R.id.addTalkLayout);
		EditText talkDate = (EditText) addTalkLayout
				.findViewById(R.id.talkDateEdit);

		talkDate.setOnFocusChangeListener(this);
		talkDate.setOnClickListener(this);

		this.talksListView.setOnItemClickListener(this);
	}

	private void initalizeAdapter() {
		this.talksListView = (ListView) findViewById(R.id.talksList);

		this.talksList = this.userData.getTalkDataList();

		this.ta = new TalksAdapter(this, talksList);

		this.talksListView.setAdapter(this.ta);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LinearLayout addTalkLayout = (LinearLayout) findViewById(R.id.addTalkLayout);
		EditText talkTitle = (EditText) addTalkLayout
				.findViewById(R.id.talkTitleEdit);
		EditText talkDate = (EditText) addTalkLayout
				.findViewById(R.id.talkDateEdit);

		TextView talkTitleText = (TextView) view.findViewById(R.id.talkTitle);
		TextView talkDateText = (TextView) view.findViewById(R.id.talkDate);

		talkTitle.setText(talkTitleText.getText().toString());
		talkDate.setText(talkDateText.getText().toString());
		talkTitle.setTag(position);
	}

	@Override
	public void postAsyncTaskCallback(String responseBody, String responseCode) {
		if (progDialog.isShowing()) {
			progDialog.dismiss();
		}

		DebugHelper.ShowMessage.d(responseBody);
		DebugHelper.ShowMessage.d(responseCode);
	}
}
