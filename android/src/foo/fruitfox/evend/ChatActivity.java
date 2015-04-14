package foo.fruitfox.evend;

import java.util.Timer;
import java.util.TimerTask;

import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCConstants;
import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCParser;
import org.schwering.irc.lib.IRCUser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import foo.fruitfox.data.UserData;
import foo.fruitfox.helpers.StorageHelper;
import foo.fruitfox.tasks.IRCChatAPITask;
import foo.fruitfox.tasks.IRCChatAPITask.AsyncResponseListener;

public class ChatActivity extends Activity implements
		IRCChatAPITask.AsyncResponseListener, IRCEventListener {

	private EditText messageBox;
	private EditText chatBox;
	private Button send;
	private ScrollView chatBoxContainer;

	private String identifier;
	private UserData userData;

	private IRCConnection connection;

	private String nickNameIRC;
	private String serverIRC;
	private String channelIRC;
	private int startPort;
	private int endPort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		identifier = StorageHelper.PreferencesHelper.getIdentifier(this);
		userData = StorageHelper.PreferencesHelper
				.getUserData(this, identifier);

		serverIRC = "irc.oftc.net";
		startPort = 6667;
		endPort = 6667;
		channelIRC = "#d41d8cd98f00b204e9800998ecf8427e";

		messageBox = (EditText) findViewById(R.id.messageBox);
		chatBox = (EditText) findViewById(R.id.chatBox);
		send = (Button) findViewById(R.id.send);
		chatBoxContainer = (ScrollView) findViewById(R.id.chatBoxContainer);

		chatBox.setKeyListener(null);
		chatBox.setTextIsSelectable(true);
		chatBox.setMovementMethod(LinkMovementMethod.getInstance());
		inputMethodManager.hideSoftInputFromWindow(chatBox.getWindowToken(), 0);

		generateIRCNick();
		initializeIRCConnection();

		chatBox.setText(Html
				.fromHtml("<font color='#007845'><em>- Connecting...</em></font><br />"));
		scrollDown();

		IRCChatAPITask task = new IRCChatAPITask(this, connection, this);
		task.execute();

		initializeConnectionCheck();
	}

	protected void onDestroy() {
		super.onDestroy();

		connection.doQuit("User closed connection.");
		connection.close();
		connection = null;
	}

	private void initializeConnectionCheck() {
		int delay = 30000; // delay for 30 sec.
		int period = 30000; // repeat every 30 secs.

		final Context context = this;
		final AsyncResponseListener listener = this;

		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {

			public void run() {

				if (connection == null || !connection.isConnected()) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							chatBox.append(Html
									.fromHtml("<font color='#007845'><em>- Trying to connect back to the server...</em></font><br />"));
							scrollDown();

						}
					});
					String currentNickName = nickNameIRC;
					if (connection != null) {
						currentNickName = connection.getNick() + "_ ";
						connection.close();
					}
					connection = null;

					initializeIRCConnection(currentNickName);
					IRCChatAPITask task = new IRCChatAPITask(context,
							connection, listener);

					task.execute();
				}
			}

		}, delay, period);

	}

	private void generateIRCNick() {
		if (userData.getRegistrationIdType().equalsIgnoreCase("email")) {
			nickNameIRC = "ehh_" + userData.getEmail().split("@")[0];
		} else {
			nickNameIRC = "ehh_" + userData.getPhone().replaceAll("[^0-9]", "");
		}

		nickNameIRC = nickNameIRC.replaceAll("[^A-Za-z0-9_]", "");

		if (nickNameIRC.length() > 28) {
			nickNameIRC.substring(0, 25);
		}
	}

	private void initializeIRCConnection() {
		if (connection == null) {
			connection = new IRCConnection(serverIRC, startPort, endPort, "",
					nickNameIRC, nickNameIRC, nickNameIRC);
			connection.addIRCEventListener((IRCEventListener) this);
			connection.setEncoding("UTF-8");
			connection.setPong(true);
			connection.setDaemon(false);
			connection.setColors(false);
		}
	}

	private void initializeIRCConnection(String newNickNameIRC) {
		if (connection == null) {
			connection = new IRCConnection(serverIRC, startPort, endPort, "",
					newNickNameIRC, nickNameIRC, nickNameIRC);
			connection.addIRCEventListener((IRCEventListener) this);
			connection.setEncoding("UTF-8");
			connection.setPong(true);
			connection.setDaemon(false);
			connection.setColors(false);
		}
	}

	private void scrollDown() {
		chatBoxContainer.smoothScrollTo(0, chatBox.getBottom());
	}

	@Override
	public void postAsyncTaskCallback(IRCConnection connection) {
		this.connection = connection;

		if (this.connection != null && this.connection.isConnected()) {
			this.connection.doJoin(channelIRC);
		}
	}

	@Override
	public void onDisconnected() {
		if (connection != null && !connection.isConnected()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					chatBox.append(Html
							.fromHtml("<br /><font color='#007845'><em>- You have been Disconnected</em></font><br />"));
					send.setText("Connect");
					messageBox.setEnabled(false);
					scrollDown();
				}
			});
		}
	}

	@Override
	public void onError(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int arg0, String arg1) {
		switch (arg0) {
		case IRCConstants.ERR_NICKCOLLISION:
		case IRCConstants.ERR_NICKNAMEINUSE:
			String currentNickName = connection.getNick();

			connection = null;

			initializeIRCConnection(currentNickName + "_");

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					chatBox.append(Html
							.fromHtml("<font color='#007845'><em>- Nickname collision, retrying with a different nick</em></font><br />"));
					scrollDown();
				}
			});

			IRCChatAPITask task = new IRCChatAPITask(this, connection, this);

			task.execute();
			break;

		case IRCConstants.ERR_ERRONEUSNICKNAME:
			break;
		}

	}

	@Override
	public void onInvite(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJoin(String arg0, IRCUser arg1) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				chatBox.append(Html.fromHtml("<b>- Joined " + channelIRC
						+ " channel</b><br/>"));
				scrollDown();
			}
		});
	}

	@Override
	public void onKick(String arg0, IRCUser arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMode(String arg0, IRCUser arg1, IRCModeParser arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMode(IRCUser arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNick(IRCUser arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotice(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPart(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPing(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrivmsg(String arg0, IRCUser arg1, String arg2) {
		final String message;

		if (arg2.startsWith("ACTION")) {
			message = "* " + arg1.getNick() + " " + arg2.substring(6);
		} else {
			message = "&lt;<font color='#003366'>" + arg1.getNick()
					+ "</font>&gt; " + arg2;
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				chatBox.append(Html.fromHtml(message + "<br />"));
				scrollDown();
			}
		});
	}

	@Override
	public void onQuit(IRCUser arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegistered() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				chatBox.append(Html
						.fromHtml("<font color='#007845'><em>- Connected to "
								+ serverIRC + "...</em></font><br />"));
				send.setText("Send");
				messageBox.setEnabled(true);
				scrollDown();
			}
		});
	}

	@Override
	public void onReply(int arg0, String arg1, String arg2) {
		String topic = "";

		final String message;

		switch (arg0) {
		case IRCConstants.RPL_TOPIC:
			topic = arg2;
			message = "- Topic: " + topic;

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					chatBox.append(Html.fromHtml(message + "<br /><br />"));
					scrollDown();
				}
			});
			break;
		}

	}

	@Override
	public void onTopic(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unknown(String arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	public void send(View view) {
		String messageText = messageBox.getText().toString();

		if (connection != null && connection.isConnected()) {
			if (messageText.trim().length() > 0) {
				if (messageText.startsWith("/")) {
					messageText = messageText.substring(1);
				}

				IRCParser parser = new IRCParser(messageText, false);
				String cmd = parser.getCommand();

				if (cmd.equalsIgnoreCase("ME")) {
					connection.doPrivmsg(channelIRC,
							IRCConstants.ACTION_INDICATOR + "ACTION "
									+ parser.getLine().substring(3) + " "
									+ IRCConstants.ACTION_INDICATOR);
					chatBox.append(Html.fromHtml("* " + connection.getNick()
							+ " " + parser.getLine().substring(3) + "<br />"));
				} else {
					connection.doPrivmsg(channelIRC, parser.getLine());
					chatBox.append(Html.fromHtml("&lt;<font color='#666666'>"
							+ connection.getNick() + "</font>&gt; "
							+ messageText + "<br />"));
				}

				messageBox.setText("");
			}
		} else {
			String currentNickName = nickNameIRC;
			if (connection != null) {
				currentNickName = connection.getNick() + "_";
				connection.close();
			}
			connection = null;

			initializeIRCConnection(currentNickName);

			IRCChatAPITask task = new IRCChatAPITask(this, connection, this);
			task.execute();

			chatBox.append(Html
					.fromHtml("<font color='#007845'><em>- Trying to connect back to the server...</em></font><br />"));
		}

		scrollDown();
	}
}
