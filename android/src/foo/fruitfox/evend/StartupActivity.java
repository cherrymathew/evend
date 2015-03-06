package foo.fruitfox.evend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.StorageHelper;

public class StartupActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);

		String identifier = StorageHelper.PreferencesHelper.getIdentifier(this);

		if (identifier != null) {
			UserData userData = StorageHelper.PreferencesHelper.getUserData(
					this, identifier);
			if (userData.getIsVerified() == true) {
				Intent intent = new Intent(this, WelcomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(intent);
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.startup, menu);
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

	public void processClick(View view) {
		Intent intent = new Intent(this, LoginActivity.class);

		int id = view.getId();
		if (id == R.id.phoneVerification) {
			intent.putExtra("type", "phone");
			startActivity(intent);
		} else if (id == R.id.emailVerification) {
			intent.putExtra("type", "email");
			startActivity(intent);
		}
	}
}
