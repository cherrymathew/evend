/**
 * Apache License Version 2.0
 * Copyright information is in the "evend" project root directory 
 */
package foo.fruitfox.evend;

import java.util.Locale;

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
import foo.fruitfox.helpers.DebugHelper;

public class LoginActivity extends ActionBarActivity {

	String registrationType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		Intent intent = getIntent();
		registrationType = intent.getStringExtra("type");
		DebugHelper.ShowMessage.t(this, "Verified by " + registrationType);

		LinearLayout activityLogin = (LinearLayout) findViewById(R.id.activity_login);
		setLayout(registrationType, activityLogin);

		LinearLayout verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);
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
		Button login = (Button) findViewById(R.id.login);
		Button register = (Button) findViewById(R.id.register);
		EditText username = (EditText) findViewById(R.id.username);
		TextView usernameLabel = (TextView) findViewById(R.id.usernameLabel);

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
			usernameLabel.setText("E-Mail");
			login.setVisibility(View.GONE);
			register.setVisibility(View.VISIBLE);
			break;
		}

		setContentView(activityLayout);
	}

	public void register(View view) {
		LinearLayout verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);
		verificationLayout.setVisibility(View.VISIBLE);
	}

	public void verify(View view) {
		Button login = (Button) findViewById(R.id.login);
		Button register = (Button) findViewById(R.id.register);
		EditText verificationCode = (EditText) findViewById(R.id.verificationCodeText);

		DebugHelper.ShowMessage.t(this, "Verification code entered was "
				+ verificationCode.getText().toString());

		login.setVisibility(View.VISIBLE);
		register.setVisibility(View.GONE);
	}

	public void login(View view) {
		DebugHelper.ShowMessage.t(this, "clicked login");
		Intent intent = new Intent(this, WelcomeActivity.class);
		startActivity(intent);
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
};
