/**
 * Apache License Version 2.0
 * Copyright information is in the "evend" project root directory 
 */
package foo.fruitfox.evend;

import java.util.Locale;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.UserDataWebAPITask;

public class LoginActivity extends ActionBarActivity implements
		UserDataWebAPITask.AsyncResponse {

	private String registrationType;

	private UserData userData;

	private Button login;
	private Button register;
	private EditText username;
	private TextView usernameLabel;
	private LinearLayout verificationLayout;
	private EditText verificationCode;

	private ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		Intent intent = getIntent();
		registrationType = intent.getStringExtra("type");

		LinearLayout activityLogin = (LinearLayout) findViewById(R.id.activity_login);
		setLayout(registrationType, activityLogin);

		verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);
		verificationLayout.setVisibility(View.GONE);

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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setLayout(String type, LinearLayout activityLayout) {
		login = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		username = (EditText) findViewById(R.id.username);
		usernameLabel = (TextView) findViewById(R.id.usernameLabel);

		switch (type) {
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
			username.setText("john@example.com");
			usernameLabel.setText("E-Mail");
			login.setVisibility(View.GONE);
			register.setVisibility(View.VISIBLE);
			break;
		}

		setContentView(activityLayout);
	}

	public void register(View view) {
		verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);
		verificationLayout.setVisibility(View.VISIBLE);
		username = (EditText) findViewById(R.id.username);
		String identifier = "";

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

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		StorageHelper.PreferencesHelper.setIdentifier(this, identifier);
		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);

		if (NetworkHelper.Utilities.isConnected(this)) {
			UserDataWebAPITask udwTask = new UserDataWebAPITask(
					LoginActivity.this);
			try {
				progDialog = ProgressDialog.show(this, "Processing...",
						"Fetching data", true, false);
				udwTask.execute("POST",
						getResources().getString(R.string.server_url)
								+ "authenticate", requestJSON.toString());

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

	public void verify(View view) {
		String identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		verificationCode = (EditText) findViewById(R.id.verificationCodeText);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		if (verificationCode.getText().toString()
				.equals(userData.getVerificationCode())) {
			UserDataWebAPITask udwTask = new UserDataWebAPITask(
					LoginActivity.this);
			try {
				progDialog = ProgressDialog.show(this, "Processing...",
						"Fetching data", true, false);
				udwTask.execute("GET",
						getResources().getString(R.string.server_url)
								+ "verify" + "?identifier=" + identifier
								+ "&code=" + userData.getVerificationCode());

			} catch (Exception e) {
				if (progDialog.isShowing()) {
					progDialog.dismiss();
				}
				udwTask.cancel(true);
			}

		} else {
			DebugHelper.ShowMessage.t(this, "Verification Code did not match");
		}
	}

	public void login(View view) {
		Intent intent = new Intent(this, WelcomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(intent);
		finish();
	}

	private String getPhoneNumber() {
		String countryCode = "";
		TelephonyManager tMgr = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mRawPhoneNumber = tMgr.getLine1Number();
		String countryISO = tMgr.getSimCountryIso().toUpperCase(Locale.ENGLISH);
		String[] countryCodeList = this.getResources().getStringArray(
				R.array.CountryCodes);
		String mPhoneNumber;

		if (mRawPhoneNumber.startsWith("+") == false) {
			for (int i = 0; i < countryCodeList.length; i++) {
				String[] countryCodePair = countryCodeList[i].split(",");
				if (countryCodePair[1].trim().equals(countryISO.trim())) {
					countryCode = countryCodePair[0];
					break;
				}
			}

			mPhoneNumber = "+" + countryCode + mRawPhoneNumber;
		} else {
			mPhoneNumber = mRawPhoneNumber;
		}

		return mPhoneNumber;
	}

	@Override
	public void postAsyncTaskCallback(String responseBody, String responseCode) {
		String identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
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
					DebugHelper.ShowMessage.d("verified");

					login = (Button) findViewById(R.id.login);
					register = (Button) findViewById(R.id.register);
					verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);
					username = (EditText) findViewById(R.id.username);

					username.setEnabled(false);
					login.setVisibility(View.VISIBLE);
					register.setVisibility(View.GONE);
					verificationLayout.setVisibility(View.GONE);

					userData.setIsVerified(true);
					userData.setAuthToken(responseJSON.getString("hhtoken"));

				} else if (responseJSON.has("error") == true) {
					DebugHelper.ShowMessage.t(this,
							"An error occured processing your request");
				}
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured processing the response");
			}
		}
		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}
}
