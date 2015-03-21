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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkHelper {
	public static class Utilities {
		public static final int RESPONSE_CODE = 0;
		public static final int RESPONSE_JSON = 1;

		public static boolean isConnected(Context context) {
			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Activity.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * @param url
		 *            to GET the Data from
		 * 
		 * @return The response HTTP Response Code and the JSON String in
		 *         position {@link RESPONSE_CODE} and {@link RESPONSE_JSON}
		 *         respectively of resultant String[].
		 */
		public static String[] HTTPGet(String url) {
			final int TIMEOUT = 15000;
			InputStream inputStream = null;
			String response = "";
			String responseCode = "";
			String[] result = new String[2];

			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);

				HttpClient httpclient = new DefaultHttpClient(httpParams);

				HttpResponse httpResponse = httpclient
						.execute(new HttpGet(url));

				responseCode += httpResponse.getStatusLine().getStatusCode();

				// Receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// Convert inputStream to string
				if (inputStream != null) {
					response = convertInputStreamToString(inputStream);
				} else {
					response = "{\"error\" : \"Connection Failure\"}";
				}

			} catch (Exception e) {
				DebugHelper.ShowMessage.d("InputStream",
						e.getLocalizedMessage());
			}

			result[0] = responseCode;
			result[1] = response;

			return result;
		}

		/**
		 * 
		 * @param url
		 *            to POST the Data to
		 * @param data
		 *            contains the data to be posted in form of a String, this
		 *            is not a generic POST we only post JSON Data.
		 * @return The response HTTP Response Code and the JSON String in
		 *         position {@link RESPONSE_CODE} and {@link RESPONSE_JSON}
		 *         respectively of resultant String[].
		 */
		public static String[] HTTPPost(String url, String data) {
			final int TIMEOUT = 15000;
			InputStream inputStream = null;
			String response = "";
			String responseCode = "";
			String[] result = new String[2];

			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);

				HttpClient httpclient = new DefaultHttpClient(httpParams);

				HttpPost httpPostRequest = new HttpPost(url);

				// Set the header information and the JSON to be posted
				StringEntity se = new StringEntity(data);
				se.setContentType("application/json;charset=UTF-8");
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json;charset=UTF-8"));
				httpPostRequest.setEntity(se);

				HttpResponse httpResponse = httpclient.execute(httpPostRequest);

				responseCode += httpResponse.getStatusLine().getStatusCode();

				// Receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// Convert inputStream to string
				if (inputStream != null) {
					response = convertInputStreamToString(inputStream);
				} else {
					response = "{\"error\" : \"Connection Failure\"}";
				}

			} catch (Exception e) {
				DebugHelper.ShowMessage.d("InputStream",
						e.getLocalizedMessage());
			}

			result[0] = responseCode;
			result[1] = response;

			return result;
		}

		/**
		 * Converts the inputStream to a proper response in String format
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
