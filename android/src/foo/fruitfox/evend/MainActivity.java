package foo.fruitfox.evend;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import foo.fruitfox.data.TalkData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.data.UserOauthData;
import foo.fruitfox.helpers.DebugHelper;
import foo.fruitfox.helpers.NetworkHelper;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.UserDataWebAPITask;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	public void buttonNext(View view) {
		Log.d("EVEND", "Clicked Next");
		Intent intent = new Intent(this, SecondActivity.class);
		startActivity(intent);
	}

	public void showLoginActivity(View view) {
		Intent intent = new Intent(this, StartupActivity.class);
		startActivity(intent);
		finish();
	}

	public void getNumber(View view) {
		String countryCode = "";
		TelephonyManager tMgr = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		String countryISO = tMgr.getSimCountryIso().toUpperCase(Locale.ENGLISH);
		String[] countryCodeList = this.getResources().getStringArray(
				R.array.CountryCodes);

		for (int i = 0; i < countryCodeList.length; i++) {
			String[] countryCodePair = countryCodeList[i].split(",");
			if (countryCodePair[1].trim().equals(countryISO.trim())) {
				countryCode = countryCodePair[0];
				break;
			}
		}
		DebugHelper.ShowMessage.t(this,
				"Phone number is " + mPhoneNumber.toString() + " "
						+ countryCode);
	}

	public void checkNetwork(View view) {
		// TODO Auto-generated method stub

		if (NetworkHelper.Utilities.isConnected(this)) {
			DebugHelper.ShowMessage.t(this, "You are connected");
		} else {
			DebugHelper.ShowMessage.t(this, "You are NOT conncted");
		}

		UserDataWebAPITask udwTask = new UserDataWebAPITask(MainActivity.this);
		try {
			// udwTask.execute("http://hmkcode.com/examples/index.php");
			// udwTask.execute("http://rest-service.guides.spring.io/greeting");
			// udwTask.execute("http://tranquil-scrubland-2285.herokuapp.com/hellos.json");
			udwTask.execute("GET",
					"https://tranquil-scrubland-2285.herokuapp.com/hellos.json");

		} catch (Exception e) {
			udwTask.cancel(true);
		}
	}

	public void putUserData(View view) {
		// UserData ud = new UserData("abc@def.com", "9876543210", "ABC123",
		// "df6c2711-2ac1-4251-aa9f-9c7f797e4c8b");
		// StorageHelper.PreferencesHelper.setUserData(this, ud.getGUID(), ud);
		StorageHelper.PreferencesHelper.clearAllUserData(this);
		DebugHelper.ShowMessage.t(this, "Cleared the User Data");
	}

	public void getUserData(View view) {
		UserData userData = new UserData("email", "a@b.com");
		UserOauthData userOauthData = new UserOauthData("Provider",
				"AccessToken", "a@b.com");
		List<TalkData> talksList = new ArrayList<TalkData>();

		for (int i = 0; i < 1; i++) {
			TalkData talkData = new TalkData("Tile " + i, "2015-09-" + (i + 1));
			talksList.add(talkData);
		}

		userData.setAuthToken("diIfbxEhdgOxL1OkG1GhWzjNnuKVrUyxF20EfVYqxbOQZfUd5uqM8pWreQOpto");
		userData.setOauthData(userOauthData);
		userData.setTalkDataList(talksList);

		// Gson gson = new Gson();
		Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
		String data = gson.toJson(userData);

		DebugHelper.ShowMessage.d(data);

		UserData restoredData;

		restoredData = gson.fromJson(data.toString(), UserData.class);

		DebugHelper.ShowMessage.d(restoredData.getAuthToken());
	}

	public void httpPost(View view) {
		DebugHelper.ShowMessage.t(this, "clicked");

		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();

		try {
			obj2.put("response", "TEST STRING");
			obj1.put("hello", obj2);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DebugHelper.ShowMessage.d(obj1.toString());

		if (NetworkHelper.Utilities.isConnected(this)) {
			DebugHelper.ShowMessage.t(this, "You are connected");
		} else {
			DebugHelper.ShowMessage.t(this, "You are NOT conncted");
		}

		UserDataWebAPITask udwTask = new UserDataWebAPITask(MainActivity.this);
		try {
			// udwTask.execute("http://hmkcode.com/examples/index.php");
			// udwTask.execute("http://rest-service.guides.spring.io/greeting");
			udwTask.execute(
					"POST",
					"https://tranquil-scrubland-2285.herokuapp.com/hellos.json",
					obj1.toString());

		} catch (Exception e) {
			udwTask.cancel(true);
		}
	}

	public void getEmail(View view) {
		DebugHelper.ShowMessage.t(this, "In email");
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
			DebugHelper.ShowMessage.t(this, account.name);
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;
				DebugHelper.ShowMessage.t(this, possibleEmail);
			}
		}
	}
}
