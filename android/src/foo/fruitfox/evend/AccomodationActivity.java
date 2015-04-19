package foo.fruitfox.evend;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import foo.fruitfox.data.AccommodationData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.UserDataWebAPITask;
import foo.fruitfox.tasks.UserDataWebAPITask.AsyncResponseListener;

public class AccomodationActivity extends ActionBarActivity implements
		OnCheckedChangeListener, AsyncResponseListener {
	private CheckBox tentCheck;
	private CheckBox sleepingBagCheck;
	private CheckBox matressCheck;
	private CheckBox pillowCheck;
	private CheckBox familyCheck;
	private EditText familyDetails;

	private ProgressDialog progDialog;

	private String identifier;
	private UserData userData;
	private AccommodationData accommodationData;

	private Boolean hasPickup;
	private Boolean hasTalk;

	private String serverURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accomodation);

		tentCheck = (CheckBox) findViewById(R.id.tentCheck);
		sleepingBagCheck = (CheckBox) findViewById(R.id.sleepingBagCheck);
		matressCheck = (CheckBox) findViewById(R.id.matressCheck);
		pillowCheck = (CheckBox) findViewById(R.id.pillowCheck);
		familyCheck = (CheckBox) findViewById(R.id.familyCheck);

		familyDetails = (EditText) findViewById(R.id.familyDetails);

		tentCheck.setFocusable(true);
		tentCheck.setFocusableInTouchMode(true);
		tentCheck.requestFocus();

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		serverURL = getResources().getString(R.string.server_url);

		Intent intent = getIntent();
		hasPickup = intent.getBooleanExtra("hasPickup", false);
		hasTalk = intent.getBooleanExtra("hasTalk", false);

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

		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		if (userData.getAccommodationData() != null) {
			accommodationData = userData.getAccommodationData();

			tentCheck.setChecked(accommodationData.getHasTent());
			sleepingBagCheck.setChecked(accommodationData.getHasSleeplingBag());
			matressCheck.setChecked(accommodationData.getHasMatress());
			pillowCheck.setChecked(accommodationData.getHasPillow());
			familyCheck.setChecked(accommodationData.getHasFamily());

			familyDetails.setText(accommodationData.getFamilyDetails());
		} else {
			accommodationData = new AccommodationData();
		}
	}

	protected void onPause() {
		super.onPause();

		userData.setAccommodationData(accommodationData);

		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	private void initializeListeners() {
		tentCheck.setOnCheckedChangeListener(this);
		sleepingBagCheck.setOnCheckedChangeListener(this);
		matressCheck.setOnCheckedChangeListener(this);
		pillowCheck.setOnCheckedChangeListener(this);
		familyCheck.setOnCheckedChangeListener(this);
	}

	public void next(View view) {
		Intent intent = null;
		JSONObject requestJSON = new JSONObject();

		if (hasPickup == true) {
			intent = new Intent(this, PickupActivity.class);
		}

		if (hasTalk == true) {
			if (intent == null) {
				intent = new Intent(this, TalksActivity.class);
			}
			intent.putExtra("hasTalk", true);
		}

		if (intent == null) {
			intent = new Intent(this, SummaryActivity.class);
		}

		if (accommodationData.getHasFamily()) {
			accommodationData.setFamilyDetails(familyDetails.getText()
					.toString());
		} else {
			accommodationData.setFamilyDetails("");
		}

		userData.setAccommodationData(accommodationData);

		try {
			requestJSON.put("hhtoken", userData.getAuthToken());
			requestJSON.put("tent", userData.getAccommodationData()
					.getHasTent() ? 1 : 0);
			requestJSON.put("sleeping_bag", userData.getAccommodationData()
					.getHasSleeplingBag() ? 1 : 0);
			requestJSON.put("mat", userData.getAccommodationData()
					.getHasMatress() ? 1 : 0);
			requestJSON.put("pillow", userData.getAccommodationData()
					.getHasPillow() ? 1 : 0);
			requestJSON.put("family", userData.getAccommodationData()
					.getHasFamily() ? 1 : 0);
			requestJSON.put("family_details", userData.getAccommodationData()
					.getFamilyDetails());
		} catch (JSONException e) {
			DebugHelper.ShowMessage.t(this,
					"An error occured processing the response");
		}

		if (NetworkHelper.Utilities.isConnected(this)) {
			UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this);
			try {
				progDialog = ProgressDialog.show(this, "Processing...",
						"Fetching data", true, false);
				udwTask.execute("POST", serverURL + "users/accommodation",
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

		startActivity(intent);
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
		} else {
			try {
				responseJSON = new JSONObject(responseBody);

				if (responseJSON.has("error") == true) {
					DebugHelper.ShowMessage.t(this,
							"An error occured processing the response");
				}
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured processing the response");
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.tentCheck:
			accommodationData.setHasTent(isChecked);
			break;

		case R.id.sleepingBagCheck:
			accommodationData.setHasSleeplingBag(isChecked);
			break;

		case R.id.matressCheck:
			accommodationData.setHasMatress(isChecked);
			break;

		case R.id.pillowCheck:
			accommodationData.setHasPillow(isChecked);
			break;

		case R.id.familyCheck:
			accommodationData.setHasFamily(isChecked);

			if (isChecked) {
				familyDetails.setVisibility(View.VISIBLE);
				ScrollView scrollWrapper = (ScrollView) findViewById(R.id.scrollWrapper);
				scrollWrapper.smoothScrollTo(0, familyDetails.getBottom());
				scrollWrapper.fullScroll(ScrollView.FOCUS_DOWN);
			} else {
				familyDetails.setVisibility(View.GONE);
				familyDetails.setText("");
			}
			break;
		}
	}
}
