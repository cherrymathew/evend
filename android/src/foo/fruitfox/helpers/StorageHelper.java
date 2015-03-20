package foo.fruitfox.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import foo.fruitfox.data.UserData;

public final class StorageHelper {
	/**
	 * @author Fox
	 * 
	 */
	public static class PreferencesHelper {
		/**
		 * @param c
		 * @param GUID
		 * @return
		 */
		public static UserData getUserData(Context c, String GUID) {
			Gson gson = Converters.registerDateTime(new GsonBuilder()).create();

			UserData ud = null;
			SharedPreferences sp = c.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			String value = sp.getString(GUID, null);

			if (value != null) {
				ud = gson.fromJson(value.toString(), UserData.class);
			}

			return ud;
		}

		public static String getIdentifier(Context c) {
			SharedPreferences sp = c.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			String value = sp.getString("identifier", null);

			return value;
		}

		/**
		 * @param c
		 * @param GUID
		 * @param ud
		 */
		public static void setUserData(Context c, String GUID, UserData ud) {
			Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
			SharedPreferences sp = c.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			String value = gson.toJson(ud);

			editor.putString(GUID, value);
			editor.commit();
		}

		public static void setIdentifier(Context c, String identifier) {
			SharedPreferences sp = c.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			editor.putString("identifier", identifier);
			editor.commit();
		}

		public static void clearAllData(Context c) {
			SharedPreferences sp = c.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			editor.clear();
			editor.commit();
		}
	}
}
