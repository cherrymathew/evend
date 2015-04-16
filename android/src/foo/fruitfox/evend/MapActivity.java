package foo.fruitfox.evend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class MapActivity extends Activity {
	private String mapURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		final ProgressDialog progDialog = ProgressDialog.show(this,
				"Please wait", "Loading OpenStreetMap...", true);
		WebView mapContainer = (WebView) findViewById(R.id.mapContainer);

		mapURL = getResources().getString(R.string.map_url);
		mapContainer.getSettings().setJavaScriptEnabled(true);
		mapContainer.getSettings().setSupportZoom(true);
		mapContainer.getSettings().setBuiltInZoomControls(true);
		mapContainer.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				if (progDialog.isShowing() && progDialog != null) {
					progDialog.dismiss();
				}
			}
		});

		mapContainer.loadUrl(mapURL);

	}
}
