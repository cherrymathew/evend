/**
 * 
 */
package foo.fruitfox.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author Fox
 * 
 */
public final class NetworkHelper {
	public static class Utilities {
		/**
		 * 
		 * @param c
		 *            Context of the activity
		 * @return
		 */
		public static boolean isConnected(Context c) {
			ConnectivityManager connMgr = (ConnectivityManager) c
					.getSystemService(Activity.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 
		 * @param url
		 *            A URL to get the Data from
		 * @return
		 */
		public static String HTTPGet(String url) {
			InputStream inputStream = null;
			String result = "";

			try {
				// create HttpClient
				HttpClient httpclient = new DefaultHttpClient();

				// make GET request to the given URL
				HttpResponse httpResponse = httpclient
						.execute(new HttpGet(url));

				// receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// convert inputStream to string
				if (inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		public static String HTTPPost(String url, String data) {
			InputStream inputStream = null;
			String result = "";

			try {
				// create HttpClient
				HttpClient httpclient = new DefaultHttpClient();

				HttpPost httpPostRequest = new HttpPost(url);

				// Set the header information and the JSON to be posted
				StringEntity se = new StringEntity(data);
				se.setContentType("application/json;charset=UTF-8");
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json;charset=UTF-8"));
				httpPostRequest.setEntity(se);

				// make POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httpPostRequest);

				// receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// convert inputStream to string
				if (inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		/**
		 * 
		 * @param inputStream
		 * @return
		 * @throws IOException
		 */
		private static String convertInputStreamToString(InputStream inputStream)
				throws IOException {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = "";
			String result = "";
			while ((line = bufferedReader.readLine()) != null) {
				result += line;
			}

			inputStream.close();
			return result;
		}
	}
}
