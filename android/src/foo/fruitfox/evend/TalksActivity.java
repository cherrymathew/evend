package foo.fruitfox.evend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
		OnClickListener, OnItemClickListener, AsyncResponseListener {

	private List<TalkData> talksList;
	private ListView talksListView;
	private TalksAdapter talksAdapter;

	private Context context;

	private UserData userData;
	private String identifier;

	private String serverURL;

	private ProgressDialog progDialog;
	private Dialog talkAddDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talks);

		context = this;
		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		serverURL = getResources().getString(R.string.server_url);

		if (talkAddDialog == null) {
			talkAddDialog = new Dialog(context);
			talkAddDialog.setContentView(R.layout.talk_add_layout);
		}

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
		if (id == R.id.action_about) {
			DebugHelper.ShowMessage.showAbout(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onResume() {
		super.onResume();
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);
		talksAdapter.notifyDataSetChanged();
	}

	protected void onPause() {
		super.onPause();

		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	public void talkRemove(View view) {
		int position = talksListView
				.getPositionForView((View) view.getParent());
		talksList.remove(position);
		talksAdapter.notifyDataSetChanged();
		userData.setTalkDataList(talksList);
		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	public void addTalk(View view) {
		talkAddDialog.setTitle("Your presentation details");

		EditText talkTitle = (EditText) talkAddDialog
				.findViewById(R.id.talkTitle);
		EditText talkDuration = (EditText) talkAddDialog
				.findViewById(R.id.talkDuration);
		CheckBox coPresenterCheck = (CheckBox) talkAddDialog
				.findViewById(R.id.coPresenterCheck);
		CheckBox projectorCheck = (CheckBox) talkAddDialog
				.findViewById(R.id.projectorCheck);
		CheckBox toolsCheck = (CheckBox) talkAddDialog
				.findViewById(R.id.toolsCheck);
		EditText talkNotes = (EditText) talkAddDialog
				.findViewById(R.id.talkNotes);

		talkDuration.setText("");
		coPresenterCheck.setChecked(false);
		projectorCheck.setChecked(false);
		toolsCheck.setChecked(false);
		talkNotes.setText("");
		talkTitle.setText("");

		talkAddDialog.show();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.saveTalk) {
			EditText talkTitle = (EditText) talkAddDialog
					.findViewById(R.id.talkTitle);
			Spinner talkType = (Spinner) talkAddDialog
					.findViewById(R.id.talkType);
			Spinner talkEvent = (Spinner) talkAddDialog
					.findViewById(R.id.talkEvent);
			EditText talkDuration = (EditText) talkAddDialog
					.findViewById(R.id.talkDuration);
			CheckBox coPresenterCheck = (CheckBox) talkAddDialog
					.findViewById(R.id.coPresenterCheck);
			CheckBox projectorCheck = (CheckBox) talkAddDialog
					.findViewById(R.id.projectorCheck);
			CheckBox toolsCheck = (CheckBox) talkAddDialog
					.findViewById(R.id.toolsCheck);
			EditText talkNotes = (EditText) talkAddDialog
					.findViewById(R.id.talkNotes);

			if (talkTitle.getText().toString().length() > 0) {
				if (talkTitle.getTag() == null) {
					TalkData talkData = new TalkData(talkTitle.getText()
							.toString());

					talkData.setType((String) talkType.getSelectedItem());
					talkData.setEvent((String) talkEvent.getSelectedItem());
					talkData.setDuration(talkDuration.getText().toString());
					talkData.setHasCoPresenters(coPresenterCheck.isChecked());
					talkData.setNeedsProjector(projectorCheck.isChecked());
					talkData.setNeedsTools(toolsCheck.isChecked());
					talkData.setNotes(talkNotes.getText().toString());

					talksList.add(talkData);
					talksAdapter.notifyDataSetChanged();

					talkAddDialog.dismiss();
				} else {
					int position = (int) talkTitle.getTag();
					talksList.get(position).setTitle(
							talkTitle.getText().toString());

					talksList.get(position).setType(
							(String) talkType.getSelectedItem());
					talksList.get(position).setEvent(
							(String) talkEvent.getSelectedItem());
					talksList.get(position).setDuration(
							talkDuration.getText().toString());
					talksList.get(position).setHasCoPresenters(
							coPresenterCheck.isChecked());
					talksList.get(position).setNeedsProjector(
							projectorCheck.isChecked());
					talksList.get(position).setNeedsTools(
							toolsCheck.isChecked());
					talksList.get(position).setNotes(
							talkNotes.getText().toString());

					talksAdapter.notifyDataSetChanged();

					talkAddDialog.dismiss();
				}

			} else {
				DebugHelper.ShowMessage.t(context,
						"Your presentation title cannot be empty");
			}

		}

		userData.setTalkDataList(talksList);
		StorageHelper.PreferencesHelper.setUserData(this, identifier, userData);
	}

	public void next(View view) {
		Intent intent = new Intent(this, SummaryActivity.class);

		JSONObject requestJSON = new JSONObject();
		JSONArray talksJSONArray = new JSONArray();

		if (talksList.size() > 0) {
			try {
				requestJSON.put("hhtoken", userData.getAuthToken());

				for (TalkData talk : talksList) {
					JSONObject talksJSON = new JSONObject();
					talksJSON.put("title", talk.getTitle());
					talksJSON.put("type", talk.getType());
					talksJSON.put("event", talk.getEvent());
					talksJSON.put("duration", talk.getDuration());
					talksJSON.put("hasCoPresenters",
							talk.getHasCoPresenters() ? 1 : 0);
					talksJSON.put("needsProjector",
							talk.getNeedsProjector() ? 1 : 0);
					talksJSON.put("needsTools", talk.getNeedsTools() ? 1 : 0);
					talksJSON.put("notes", talk.getNotes());
					talksJSONArray.put(talksJSON);
				}

				requestJSON.put("talks", talksJSONArray);

				if (NetworkHelper.Utilities.isConnected(this)) {
					UserDataWebAPITask udwTask = new UserDataWebAPITask(this,
							this);
					try {
						progDialog = ProgressDialog.show(this,
								"Please wait...", "Saving your data", true,
								false);
						udwTask.execute("POST", serverURL + "users/talk/add",
								requestJSON.toString());

					} catch (Exception e) {
						if (progDialog.isShowing()) {
							progDialog.dismiss();
						}
						udwTask.cancel(true);
					}
				} else {
					DebugHelper.ShowMessage.t(this,
							"Unable to connect to the server.");
				}
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured while creating the JSON request.");
				DebugHelper.ShowMessage.d("TalksActivity", e.getMessage());
			}

			startActivity(intent);
		} else {
			DebugHelper.ShowMessage.t(this, "You have not entered any talks.");
		}
	}

	private void initializeListeners() {
		LinearLayout talkAddLayout = (LinearLayout) talkAddDialog
				.findViewById(R.id.talkAddLayout);
		Button saveTalk = (Button) talkAddLayout.findViewById(R.id.saveTalk);

		saveTalk.setOnClickListener(this);

		talksListView.setOnItemClickListener(this);
	}

	private void initalizeAdapter() {
		List<String> talkTypeList = new ArrayList<String>();
		List<String> talkEventList = new ArrayList<String>();

		ArrayAdapter<String> talkTypeAdapter;
		ArrayAdapter<String> talkEventAdapter;

		String[] talkTypeArray = getResources().getStringArray(
				R.array.talk_types);
		String[] talkEventArray = getResources().getStringArray(
				R.array.talk_events);

		LinearLayout talkAddLayout = (LinearLayout) talkAddDialog
				.findViewById(R.id.talkAddLayout);
		Spinner talkType = (Spinner) talkAddLayout.findViewById(R.id.talkType);
		Spinner talkEvent = (Spinner) talkAddLayout
				.findViewById(R.id.talkEvent);

		for (int i = 0; i < talkTypeArray.length; i++) {
			talkTypeList.add(talkTypeArray[i]);
		}

		for (int i = 0; i < talkEventArray.length; i++) {
			talkEventList.add(talkEventArray[i]);
		}

		talkTypeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, talkTypeList);
		talkTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
		talkType.setAdapter(talkTypeAdapter);

		talkEventAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, talkEventList);
		talkEventAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
		talkEvent.setAdapter(talkEventAdapter);

		talksListView = (ListView) findViewById(R.id.talksList);

		talksList = userData.getTalkDataList();

		talksAdapter = new TalksAdapter(this, talksList);

		talksListView.setAdapter(talksAdapter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ArrayAdapter<String> talkTypeAdapter;
		ArrayAdapter<String> talkEventAdapter;

		EditText talkTitle = (EditText) talkAddDialog
				.findViewById(R.id.talkTitle);
		Spinner talkType = (Spinner) talkAddDialog.findViewById(R.id.talkType);
		Spinner talkEvent = (Spinner) talkAddDialog
				.findViewById(R.id.talkEvent);
		EditText talkDuration = (EditText) talkAddDialog
				.findViewById(R.id.talkDuration);
		CheckBox coPresenterCheck = (CheckBox) talkAddDialog
				.findViewById(R.id.coPresenterCheck);
		CheckBox projectorCheck = (CheckBox) talkAddDialog
				.findViewById(R.id.projectorCheck);
		CheckBox toolsCheck = (CheckBox) talkAddDialog
				.findViewById(R.id.toolsCheck);
		EditText talkNotes = (EditText) talkAddDialog
				.findViewById(R.id.talkNotes);

		TextView rowTalkTitle = (TextView) view.findViewById(R.id.talkTitle);
		TextView rowTalkType = (TextView) view.findViewById(R.id.talkType);
		TextView rowTalkDuration = (TextView) view
				.findViewById(R.id.talkDuration);
		TextView rowTalkEvent = (TextView) view.findViewById(R.id.talkEvent);
		TextView rowTalkHasCoPresenters = (TextView) view
				.findViewById(R.id.talkHasCoPresenters);
		TextView rowTalkNeedsProjector = (TextView) view
				.findViewById(R.id.talkNeedsProjector);
		TextView rowTalkNeedsTools = (TextView) view
				.findViewById(R.id.talkNeedsTools);
		TextView rowTalkNotes = (TextView) view.findViewById(R.id.talkNotes);

		talkAddDialog.setTitle("Edit your presentation");

		talkTypeAdapter = (ArrayAdapter<String>) talkType.getAdapter();
		talkEventAdapter = (ArrayAdapter<String>) talkEvent.getAdapter();

		talkTitle.setText(rowTalkTitle.getText().toString());
		talkType.setSelection(talkTypeAdapter.getPosition((String) rowTalkType
				.getText()));
		talkEvent.setSelection(talkEventAdapter
				.getPosition((String) rowTalkEvent.getText()));
		talkDuration.setText(rowTalkDuration.getText().toString());
		coPresenterCheck
				.setChecked(rowTalkHasCoPresenters.getText() == "Yes" ? true
						: false);
		projectorCheck
				.setChecked(rowTalkNeedsProjector.getText() == "Yes" ? true
						: false);
		toolsCheck.setChecked(rowTalkNeedsTools.getText() == "Yes" ? true
				: false);
		talkNotes.setText(rowTalkNotes.getText().toString());

		talkTitle.setTag(position);

		talkAddDialog.show();
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
			DebugHelper.ShowMessage.d("TalksActivity", "Response Code : "
					+ responseCode);
			DebugHelper.ShowMessage.d("TalksActivity", "Response Body : "
					+ responseBody);
		} else {
			try {
				responseJSON = new JSONObject(responseBody);

				if (responseJSON.has("error") == true) {
					DebugHelper.ShowMessage.t(this,
							responseJSON.getString("error"));
					DebugHelper.ShowMessage.d("TalksActivity",
							"Response Code :" + responseCode);
					DebugHelper.ShowMessage.d("TalksActivity",
							"Response Body :" + responseBody);
				}
			} catch (JSONException e) {
				DebugHelper.ShowMessage.t(this,
						"An error occured trying to parse the JSON response");
				DebugHelper.ShowMessage.d("TalksActivity", "Response Code : "
						+ responseCode);
				DebugHelper.ShowMessage.d("TalksActivity", "Response Body : "
						+ responseBody);
				DebugHelper.ShowMessage.d("TalksActivity", e.getMessage());
			}
		}
	}
}
