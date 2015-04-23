/**
 * Apache License Version 2.0
 * Copyright information is in the "evend" project root directory 
 */
package foo.fruitfox.evend;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import foo.fruitfox.data.AccommodationData;
import foo.fruitfox.data.PickupData;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.UserDataWebAPITask;
import foo.fruitfox.tasks.UserDataWebAPITask.AsyncJobNotifier;

public class LoginActivity extends ActionBarActivity implements
		UserDataWebAPITask.AsyncResponseListener,
		UserDataWebAPITask.AsyncJobNotifierAccessors {

	private String registrationType;
	private String serverURL;

	private UserData userData;
	private String identifier;

	private Button login;
	private Button register;
	private EditText username;
	private TextView usernameLabel;
	private LinearLayout verificationLayout;
	private ScrollView activityLayout;
	private EditText verificationCode;

	private ProgressDialog progDialog;

	private AsyncJobNotifier unitTestNotifier;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		Intent intent = getIntent();

		registrationType = intent.getStringExtra("type");

		serverURL = getResources().getString(R.string.server_url);

		activityLayout = (ScrollView) findViewById(R.id.activity_login);

		verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);
		verificationLayout.setVisibility(View.GONE);

		initializeLayout();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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

	private void initializeLayout() {
		login = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		username = (EditText) findViewById(R.id.username);
		usernameLabel = (TextView) findViewById(R.id.usernameLabel);

		switch (registrationType) {
		case "phone":
			username.setInputType(InputType.TYPE_CLASS_PHONE);
			username.setText(getPhoneNumber());
			usernameLabel.setText("Phone Number");
			login.setVisibility(View.GONE);
			register.setVisibility(View.VISIBLE);
			break;

		case "email":
			username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			// TODO: Remove this Hardcoding
			// username.setText("john@example.com");
			usernameLabel.setText("E-Mail");
			login.setVisibility(View.GONE);
			register.setVisibility(View.VISIBLE);
			break;
		}

		setContentView(activityLayout);
	}

	public void register(View view) {
		final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
				"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
				Pattern.CASE_INSENSITIVE);
		verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);
		verificationLayout.setVisibility(View.VISIBLE);
		username = (EditText) findViewById(R.id.username);
		identifier = "";

		JSONObject requestJSON = new JSONObject();

		try {
			switch (registrationType) {
			case "email":
				String email = username.getText().toString();
				if (userData == null) {
					userData = new UserData(registrationType, email);
				} else {
					userData.setEmail(email);
				}
				identifier = email;
				requestJSON.put("email", email);
				break;

			case "phone":
				String phone = username.getText().toString();
				if (userData == null) {
					userData = new UserData(registrationType, phone);
				} else {
					userData.setPhone(phone);
				}
				identifier = phone;
				requestJSON.put("phone", phone);
				break;
			}

		} catch (JSONException e) {
			DebugHelper.ShowMessage.t(this,
					"An error occured while creating the JSON request.");
			DebugHelper.ShowMessage.d("LoginActivity", e.getMessage());
		}

		if (userData.getEmail().toString().length() > 0
				&& VALID_EMAIL_ADDRESS_REGEX.matcher(
						userData.getEmail().toString()).find()) {

			StorageHelper.PreferencesHelper.setIdentifier(this, identifier);
			StorageHelper.PreferencesHelper.setUserData(this, identifier,
					userData);

			if (NetworkHelper.Utilities.isConnected(this)) {
				UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this,
						this);
				try {
					progDialog = ProgressDialog.show(this,
							"Processing email...",
							"Sending you a verification code.", true, false);
					udwTask.execute("POST", serverURL + "authenticate",
							requestJSON.toString());
					DebugHelper.ShowMessage.t(this,
							"You may need to check your *SPAM* folder.");

				} catch (Exception e) {
					if (progDialog.isShowing()) {
						progDialog.dismiss();
					}
					udwTask.cancel(true);
					DebugHelper.ShowMessage
							.t(this,
									"An error occurred while processing your request. Please try again later.");
					DebugHelper.ShowMessage.d("LoginActivity", e.getMessage());
				}
			} else {
				DebugHelper.ShowMessage.t(this,
						"Unable to connect to the server.");
			}
		} else {
			DebugHelper.ShowMessage.t(this, "Please enter a valid E-Mail.");
		}
	}

	public void verify(View view) {
		verificationCode = (EditText) findViewById(R.id.verificationCode);

		if (verificationCode.getText().toString()
				.equals(userData.getVerificationCode())) {
			if (NetworkHelper.Utilities.isConnected(this)) {
				UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this,
						this);
				try {
					progDialog = ProgressDialog.show(this, "Please wait...",
							"Verifying Email", true, false);
					udwTask.execute(
							"GET",
							serverURL + "verify" + "?identifier="
									+ URLEncoder.encode(identifier, "UTF-8")
									+ "&code=" + userData.getVerificationCode());

				} catch (Exception e) {
					if (progDialog.isShowing()) {
						progDialog.dismiss();
					}
					udwTask.cancel(true);
					DebugHelper.ShowMessage
							.t(this,
									"An error occurred while processing your request. Please try again later.");
					DebugHelper.ShowMessage.d("LoginActivity", e.getMessage());
				}

			} else {
				DebugHelper.ShowMessage.t(this,
						"Unable to connect to the server.");
				DebugHelper.ShowMessage.d("LoginActivity", "Entered code: "
						+ verificationCode.getText().toString()
						+ ", Received code: " + userData.getVerificationCode());
			}
		} else {
			DebugHelper.ShowMessage.t(this, "Verification Code did not match.");
		}
	}

	public void login(View view) {
		if (NetworkHelper.Utilities.isConnected(this)) {
			UserDataWebAPITask udwTask = new UserDataWebAPITask(this, this,
					this);
			try {
				progDialog = ProgressDialog.show(this, "Please wait...",
						"Logging you in", true, false);
				udwTask.execute("GET", serverURL + "users/summary/"
						+ URLEncoder.encode(identifier, "UTF-8"));

			} catch (Exception e) {
				if (progDialog.isShowing()) {
					progDialog.dismiss();
				}
				udwTask.cancel(true);
				DebugHelper.ShowMessage
						.t(this,
								"An error occurred while processing your request. Please try again later.");
				DebugHelper.ShowMessage.d("LoginActivity", e.getMessage());
			}

		} else {
			DebugHelper.ShowMessage.t(this, "Unable to connect to the server.");
		}
	}

	private String getPhoneNumber() {
		String countryCode = "";
		TelephonyManager tMgr = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mRawPhoneNumber = tMgr.getLine1Number();
		String countryISO = tMgr.getSimCountryIso().toUpperCase(Locale.ENGLISH);
		String[] countryCodeList = getResources().getStringArray(
				R.array.country_codes);
		String mPhoneNumber = "";

		for (int i = 0; i < countryCodeList.length; i++) {
			String[] countryCodePair = countryCodeList[i].split(",");
			if (countryCodePair[1].trim().equals(countryISO.trim())) {
				countryCode = countryCodePair[0];
				break;
			}
		}

		if (mRawPhoneNumber != null) {
			if (mRawPhoneNumber.startsWith("+") == false) {
				mPhoneNumber = "+" + countryCode + mRawPhoneNumber;
			} else {
				mPhoneNumber = mRawPhoneNumber;
			}
		} else {
			mPhoneNumber = "+" + countryCode;
		}

		return mPhoneNumber;
	}

	@Override
	public void postAsyncTaskCallback(String responseBody, String responseCode) {
		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		JSONObject responseJSON;

		if (progDialog.isShowing()) {
			progDialog.dismiss();
		}

		if (responseBody.length() == 0) {
			DebugHelper.ShowMessage
					.t(this,
							"There was an error processing your request. Please try again later.");
			DebugHelper.ShowMessage.d("LoginActivity", "Response Code : "
					+ responseCode);
			DebugHelper.ShowMessage.d("LoginActivity", "Response Body : "
					+ responseBody);
		} else {
			try {
				responseJSON = new JSONObject(responseBody);

				if (responseJSON.has("needverfication") == true) {
					Boolean needVerificationStatus = responseJSON
							.getBoolean("needverfication");
					String verifier = "";
					if (needVerificationStatus == true) {
						verifier = responseJSON.getJSONObject("user")
								.getString("verifier");
						userData.setVerificationCode(verifier);
					}
				} else if (responseJSON.has("hhtoken") == true) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

					login = (Button) findViewById(R.id.login);
					register = (Button) findViewById(R.id.register);
					verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);
					username = (EditText) findViewById(R.id.username);

					username.setEnabled(false);
					username.setInputType(InputType.TYPE_NULL);
					username.setFocusable(false);
					imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
					login.setVisibility(View.VISIBLE);
					login.requestFocus();
					register.setVisibility(View.GONE);
					verificationLayout.setVisibility(View.GONE);

					userData.setIsVerified(false);
					userData.setAuthToken(responseJSON.getString("hhtoken"));

				} else if (responseJSON.has("error") == true) {
					DebugHelper.ShowMessage.t(this,
							responseJSON.getString("error"));
					DebugHelper.ShowMessage.d("LoginActivity",
							"Response Code :" + responseCode);
					DebugHelper.ShowMessage.d("LoginActivity",
							"Response Body :" + responseBody);
				} else if (responseJSON.has("identifier") == true) {
					Intent intent = null;
					if (responseJSON.has("username")) {
						userData.setFirstName(responseJSON
								.getString("username"));
					}

					if (responseJSON.getJSONArray("attendance").length() > 0) {
						JSONObject attendanceJSON = (JSONObject) responseJSON
								.getJSONArray("attendance").get(0);
						userData.setAttendanceStartDate("dd-MM-yyyy",
								attendanceJSON.getString("arrival_date"));
						userData.setAttendanceEndDate("dd-MM-yyyy",
								attendanceJSON.getString("departure_date"));
						userData.setNeedsAccommodation(attendanceJSON
								.getInt("accommodation") == 1 ? true : false);
						userData.setNeedsPickUp(attendanceJSON.getInt("pickup") == 1 ? true
								: false);
						userData.setHasTalk(attendanceJSON.getInt("talk") == 1 ? true
								: false);

						userData.setEventDaysAttending(new DateTime(
								"2015-05-07"), new DateTime("2015-06-07"));
					}

					if (userData.getNeedsAccommodation() == true
							&& responseJSON.getJSONArray("accommodation")
									.length() > 0) {
						JSONObject accommodationJSON = (JSONObject) responseJSON
								.getJSONArray("accommodation").get(0);
						AccommodationData accommodationData = new AccommodationData();

						accommodationData.setHasTent(accommodationJSON
								.getInt("tent") == 1 ? true : false);
						accommodationData.setHasSleeplingBag(accommodationJSON
								.getInt("sleeping_bag") == 1 ? true : false);
						accommodationData.setHasMatress(accommodationJSON
								.getInt("mat") == 1 ? true : false);
						accommodationData.setHasPillow(accommodationJSON
								.getInt("pillow") == 1 ? true : false);
						accommodationData.setHasFamily(accommodationJSON
								.getInt("family") == 1 ? true : false);
						accommodationData.setFamilyDetails(accommodationJSON
								.getString("family_details"));

						userData.setAccommodationData(accommodationData);
					}

					if (userData.getNeedsPickUp() == true
							&& responseJSON.getJSONArray("pickup").length() > 0) {
						JSONObject pickupJSON = (JSONObject) responseJSON
								.getJSONArray("pickup").get(0);
						PickupData pickupData = new PickupData();

						pickupData
								.setLocation(pickupJSON.getString("location"));
						pickupData.setPickupDate("dd-MM-yyyy",
								pickupJSON.getString("date"));
						pickupData.setPickupTime("HH:mm",
								pickupJSON.getString("time"));
						pickupData.setSeatsCount(String.valueOf(pickupJSON
								.getInt("seats")));

						userData.setPickupData(pickupData);
					}

					if (userData.getHasTalk() == true
							&& responseJSON.getJSONArray("talks").length() > 0) {
						JSONArray talksJSONArray = responseJSON
								.getJSONArray("talks");
						List<TalkData> talkDataList = new ArrayList<TalkData>();

						for (int i = 0; i < talksJSONArray.length(); i++) {
							TalkData talkData = new TalkData();
							JSONObject talkDataJSON = (JSONObject) talksJSONArray
									.get(i);

							talkData.setTitle(talkDataJSON.getString("title"));
							talkData.setType(talkDataJSON.getString("type"));
							talkData.setEvent(talkDataJSON.getString("event"));
							talkData.setDuration(talkDataJSON
									.getString("duration"));
							talkData.setNotes(talkDataJSON.getString("notes"));
							talkData.setHasCoPresenters(talkDataJSON
									.getInt("hasCoPresenters") == 1 ? true
									: false);
							talkData.setNeedsProjector(talkDataJSON
									.getInt("needsProjector") == 1 ? true
									: false);
							talkData.setNeedsTools(talkDataJSON
									.getInt("needsTools") == 1 ? true : false);

							talkDataList.add(talkData);
						}

						userData.setTalkDataList(talkDataList);
					}

					userData.setIsFinalized(responseJSON.getInt("confirmed") == 1 ? true
							: false);

					userData.setIsVerified(true);

					StorageHelper.PreferencesHelper.setIdentifier(this,
							identifier);
					StorageHelper.PreferencesHelper.setUserData(this,
							identifier, userData);

					if (userData.getIsFinalized() == false) {
						intent = new Intent(this, WelcomeActivity.class);
					} else {
						intent = new Intent(this, SummaryActivity.class);
					}

					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(intent);
					finish();
				}
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured trying to parse the JSON response");
				DebugHelper.ShowMessage.d("LoginActivity", "Response Code : "
						+ responseCode);
				DebugHelper.ShowMessage.d("LoginActivity", "Response Body : "
						+ responseBody);
				DebugHelper.ShowMessage.d("LoginActivity", e.getMessage());
			}
		}
		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	@Override
	public AsyncJobNotifier getUnitTestNotifier() {
		return this.unitTestNotifier;
	}

	@Override
	public void setUnitTestNotifier(AsyncJobNotifier unitTestNotifier) {
		this.unitTestNotifier = unitTestNotifier;
	}
}
