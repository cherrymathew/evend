package foo.fruitfox.evend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import foo.fruitfox.adapters.EventCalendarAdapter;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.StorageHelper;

public class SummaryActivity extends ActionBarActivity implements
		DialogInterface.OnClickListener {
	private Map<String, String> summaryMap;

	private LinearLayout accommodationSummary;
	private LinearLayout pickupSummary;
	private LinearLayout talksSummary;

	private GridView eventCalendarGrid;

	AlertDialog.Builder finalizeAlertDialogBuilder;

	private Context context;

	private EventCalendarAdapter eventCalendarAdapter;

	private String identifier;
	private UserData userData;

	private String startDateString = "2015-05-07";
	private String endDateString = "2015-06-07";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		context = this;

		finalizeAlertDialogBuilder = new AlertDialog.Builder(context);

		eventCalendarGrid = (GridView) findViewById(R.id.attendanceSummaryGrid);

		accommodationSummary = (LinearLayout) findViewById(R.id.accommodationSummary);
		pickupSummary = (LinearLayout) findViewById(R.id.pickupSummary);
		talksSummary = (LinearLayout) findViewById(R.id.talksSummary);

		initalizeLayout();
		initalizeAdapters();
		initalizeListeners();
		initializeAccommodationSummary();
		initializePickupSummary();
		initializeTalksSummary();
	}

	private void initalizeLayout() {
		String finalizeDialogMessage = getResources().getString(
				R.string.summary_finalize_dialog_message);
		finalizeAlertDialogBuilder.setMessage(finalizeDialogMessage);

		TextView username = (TextView) findViewById(R.id.name);

		username.setText(userData.getFirstName());

		if (userData.getIsFinalized() == true) {
			Button openMap = (Button) findViewById(R.id.openMap);
			Button finalize = (Button) findViewById(R.id.finalize);

			openMap.setVisibility(View.VISIBLE);
			finalize.setVisibility(View.GONE);
		}
	}

	private void initalizeListeners() {
		finalizeAlertDialogBuilder.setPositiveButton("Yes", this);
		finalizeAlertDialogBuilder.setNegativeButton("No", this);
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

		summaryMap = new LinkedHashMap<String, String>();

		if (userData.getAccommodationData() != null) {
			summaryMap
					.put(getResources().getString(
							R.string.accommodation_tent_check_label), userData
							.getAccommodationData().getHasTent() ? "Yes" : "No");
			summaryMap
					.put(getResources().getString(
							R.string.accommodation_sleeping_bag_check_label),
							userData.getAccommodationData()
									.getHasSleeplingBag() ? "Yes" : "No");
			summaryMap.put(
					getResources().getString(
							R.string.accommodation_matress_check_label),
					userData.getAccommodationData().getHasMatress() ? "Yes"
							: "No");
			summaryMap.put(
					getResources().getString(
							R.string.accommodation_pillow_check_label),
					userData.getAccommodationData().getHasPillow() ? "Yes"
							: "No");
			summaryMap.put(
					getResources().getString(
							R.string.accommodation_family_check_label),
					userData.getAccommodationData().getHasFamily() ? "Yes"
							: "No");

			if (userData.getAccommodationData().getHasFamily()) {
				String familyDetails = userData.getAccommodationData()
						.getFamilyDetails();
				if (familyDetails.trim().length() == 0) {
					familyDetails = "N/A";
				}
				summaryMap
						.put(getResources().getString(
								R.string.summary_family_details), familyDetails);
			}
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

		summaryMap = new LinkedHashMap<String, String>();

		if (userData.getPickupData() != null) {
			summaryMap.put(
					getResources().getString(R.string.pickup_location_label),
					userData.getPickupData().getLocation());
			summaryMap.put(
					getResources().getString(R.string.pickup_date_label),
					userData.getPickupData().getPickupDate("dd-MM-yyyy"));
			summaryMap.put(
					getResources().getString(R.string.pickup_time_label),
					userData.getPickupData().getPickupTime("HH:mm"));
			summaryMap.put(getResources()
					.getString(R.string.pickup_seats_label), userData
					.getPickupData().getSeatsCount());
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

		summaryMap = new LinkedHashMap<String, String>();

		List<TalkData> talkDataList = userData.getTalkDataList();

		if (talkDataList.size() > 0) {
			for (int i = 0; i < talkDataList.size(); i++) {
				summaryMap.put(talkDataList.get(i).getTitle(), "A "
						+ talkDataList.get(i).getType() + " for "
						+ talkDataList.get(i).getEvent());
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

	public void finalize(View view) {
		finalizeAlertDialogBuilder.show();
	}

	public void openMap(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}

	public void liveChat(View view) {
		Intent intent = new Intent(this, ChatActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			userData.setIsFinalized(true);

			Intent intent = new Intent(this, SummaryActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(intent);
			finish();
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			userData.setIsFinalized(false);
			break;
		}

		StorageHelper.PreferencesHelper.setUserData(context, identifier,
				userData);
	}
}
