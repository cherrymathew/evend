package foo.fruitfox.evend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import foo.fruitfox.adapters.EventCalendarAdapter;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.StorageHelper;

public class SummaryActivity extends ActionBarActivity {
	private Map<String, String> summaryMap;

	private LinearLayout accommodationSummary;
	private LinearLayout pickupSummary;
	private LinearLayout talksSummary;

	private GridView eventCalendarGrid;

	private Context context;

	private EventCalendarAdapter eventCalendarAdapter;

	private String identifier;
	private UserData userData;

	private String startDateString = "2015-05-23";
	private String endDateString = "2015-06-07";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		context = this;

		eventCalendarGrid = (GridView) findViewById(R.id.attendanceSummaryGrid);

		accommodationSummary = (LinearLayout) findViewById(R.id.accommodationSummary);
		pickupSummary = (LinearLayout) findViewById(R.id.pickupSummary);
		talksSummary = (LinearLayout) findViewById(R.id.talksSummary);

		initalizeAdapters();
		initializeAccommodationSummary();
		initializePickupSummary();
		initializeTalksSummary();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
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

	private void initializeAccommodationSummary() {
		View summarySingleRow;
		TextView itemTitle;
		TextView itemDetail;

		summaryMap = new HashMap<String, String>();

		if (userData.getAccommodationData() != null) {
			summaryMap.put("Type of Accommodation", userData
					.getAccommodationData().getAccommodationType());
			summaryMap.put("Starting date of Booking", userData
					.getAccommodationData().getStartDate("dd-MM-yyyy"));
			summaryMap.put("Duration of the booking", userData
					.getAccommodationData().getDaysCount());
			summaryMap.put("Total number of people", userData
					.getAccommodationData().getBedsCount());
		} else {
			summaryMap.put("You have not selected any items for this", "");
		}

		for (Map.Entry<String, String> item : summaryMap.entrySet()) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			summarySingleRow = inflater.inflate(R.layout.summary_single_row,
					accommodationSummary, false);

			itemTitle = (TextView) summarySingleRow
					.findViewById(R.id.itemTitle);
			itemDetail = (TextView) summarySingleRow
					.findViewById(R.id.itemDetail);

			itemTitle.setText(item.getKey());
			itemDetail.setText(item.getValue());

			accommodationSummary.addView(summarySingleRow);

		}
	}

	private void initializePickupSummary() {
		View summarySingleRow;
		TextView itemTitle;
		TextView itemDetail;

		summaryMap = new HashMap<String, String>();

		if (userData.getPickupData() != null) {
			summaryMap.put("Preferred pickup Location", userData
					.getPickupData().getLocation());
			summaryMap.put("Your arrival date", userData.getPickupData()
					.getPickupDate("dd-MM-yyyy"));
			summaryMap.put("Your arrival time", userData.getPickupData()
					.getPickupTime("HH:mm"));
			summaryMap.put("Total number of people", userData.getPickupData()
					.getSeatsCount());
		} else {
			summaryMap.put("You have not selected any items for this", "");
		}

		for (Map.Entry<String, String> item : summaryMap.entrySet()) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			summarySingleRow = inflater.inflate(R.layout.summary_single_row,
					pickupSummary, false);

			itemTitle = (TextView) summarySingleRow
					.findViewById(R.id.itemTitle);
			itemDetail = (TextView) summarySingleRow
					.findViewById(R.id.itemDetail);

			itemTitle.setText(item.getKey());
			itemDetail.setText(item.getValue());

			pickupSummary.addView(summarySingleRow);
		}
	}

	private void initializeTalksSummary() {
		View summarySingleRow;
		TextView itemTitle;
		TextView itemDetail;

		summaryMap = new HashMap<String, String>();

		List<TalkData> talkDataList = userData.getTalkDataList();

		if (talkDataList.size() > 0) {
			for (int i = 0; i < talkDataList.size(); i++) {
				summaryMap.put(talkDataList.get(i).getTitle(), talkDataList
						.get(i).getDate("dd-MM-yyyy"));
			}
		} else {
			summaryMap.put("You are not presenting any talks.", "");
		}

		for (Map.Entry<String, String> item : summaryMap.entrySet()) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			summarySingleRow = inflater.inflate(R.layout.summary_single_row,
					talksSummary, false);

			itemTitle = (TextView) summarySingleRow
					.findViewById(R.id.itemTitle);
			itemDetail = (TextView) summarySingleRow
					.findViewById(R.id.itemDetail);

			itemTitle.setText(item.getKey());
			itemDetail.setText(item.getValue());

			talksSummary.addView(summarySingleRow);
		}
	}

	private void initalizeAdapters() {
		eventCalendarAdapter = new EventCalendarAdapter(this, startDateString,
				endDateString, userData.getEventDaysAttending());

		eventCalendarGrid.setAdapter(eventCalendarAdapter);
	}
}
