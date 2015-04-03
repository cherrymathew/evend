package foo.fruitfox.evend;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import foo.fruitfox.data.AccommodationData;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.StorageHelper;

public class AccomodationActivity extends ActionBarActivity implements
		OnItemSelectedListener {
	private List<String> accommodationTypesList;
	private List<String> bedsCountList;

	private ArrayAdapter<String> accommodationTypesAdapter;
	private ArrayAdapter<String> bedsCountAdapter;

	private Spinner accommodationTypes;
	private Spinner bedsCount;

	private String identifier;
	private UserData userData;
	private AccommodationData accommodationData;

	private boolean hasPickup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accomodation);

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		accommodationTypes = (Spinner) findViewById(R.id.accommodationTypes);
		bedsCount = (Spinner) findViewById(R.id.bedsCount);

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

		} else {
			accommodationData = new AccommodationData();
		}
	}

	protected void onPause() {
		super.onPause();

		userData.setAccommodationData(accommodationData);

		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	private void initializeAdapters() {
		accommodationTypesList = new ArrayList<String>();
		accommodationTypesList.add("Room");
		accommodationTypesList.add("Bed");
		accommodationTypesList.add("Tent");

		bedsCountList = new ArrayList<String>();

		for (int i = 1; i < 6; i++) {
			bedsCountList.add("" + i);
		}

		accommodationTypesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, accommodationTypesList);

		accommodationTypesAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

		bedsCountAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, bedsCountList);

		bedsCountAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

		accommodationTypes.setAdapter(accommodationTypesAdapter);
		bedsCount.setAdapter(bedsCountAdapter);
	}

	private void initializeListeners() {
		accommodationTypes.setOnItemSelectedListener(this);
		bedsCount.setOnItemSelectedListener(this);
	}

	public void next(View view) {
		Intent intent;
		if (hasPickup == true) {
			intent = new Intent(this, PickupActivity.class);
		} else {
			intent = new Intent(this, TalksActivity.class);
		}

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
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
