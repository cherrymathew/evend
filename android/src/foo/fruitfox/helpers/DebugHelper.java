package foo.fruitfox.helpers;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import foo.fruitfox.evend.R;

public final class DebugHelper {
	public static class ShowMessage {
		public static void d(String message) {
			Log.d("EVEND", message);
		}

		public static void d(String tag, String message) {
			Log.d("EVEND " + tag, message);
		}

		public static void t(Context context, String message) {
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, message, duration);
			toast.show();
		}

		public static void showAbout(final Context context) {
			Dialog aboutDialog = new Dialog(context);
			aboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			aboutDialog.setContentView(R.layout.about_layout);

			TextView evendLink = (TextView) aboutDialog
					.findViewById(R.id.evend_link);
			evendLink.setMovementMethod(LinkMovementMethod.getInstance());
			evendLink
					.setText(Html
							.fromHtml("<a href='https://github.com/cherrymathew/evend'>https://github.com/cherrymathew/evend</a>"));
			evendLink.setFocusable(true);
			ImageView aboutImage = (ImageView) aboutDialog
					.findViewById(R.id.about_image);
			aboutImage.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View view) {
					ImageView aboutImage = (ImageView) view;
					aboutImage.setImageResource(R.drawable.logo);
					aboutImage.getLayoutParams().height = 500;
					aboutImage.getLayoutParams().width = 500;
					aboutImage.requestLayout();
					return true;
				}
			});
			aboutDialog.show();

			// Grab the window of the dialog, and change the width
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			Window window = aboutDialog.getWindow();
			lp.copyFrom(window.getAttributes());
			// This makes the dialog take up the full width
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			window.setAttributes(lp);
		}
	}
}
