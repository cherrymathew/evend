package foo.fruitfox.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import foo.fruitfox.data.UserData;

public final class StorageHelper {
	public static class PreferencesHelper {

		/**
		 * @param context
		 *            of the running activity
		 * @param identifier
		 *            is the id used by the system to uniquely identify the user
		 *            within the system
		 * @return The UserData object is returned if found else a null is
		 *         returned
		 */
		public static UserData getUserData(Context context, String identifier) {
			Gson gson = Converters.registerDateTime(new GsonBuilder()).create();

			UserData userData = null;
			SharedPreferences sp = context.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			String value = sp.getString(identifier, null);

			if (value != null) {
				userData = gson.fromJson(value.toString(), UserData.class);
			}

			return userData;
		}

		/**
		 * 
		 * @param context
		 *            of the running activity
		 * @return The identifier that uniquely identifies the user within the
		 *         system
		 */
		public static String getIdentifier(Context context) {
			SharedPreferences sp = context.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			String value = sp.getString("identifier", null);

			return value;
		}

		/**
		 * @param context
		 *            of the running activity.
		 * @param identifer
		 *            is the id used by the system to uniquely identify the user
		 *            within the system.
		 * @param userData
		 *            The UserData object that is going to be stored in the
		 *            SharedPreferences.
		 */
		public static void setUserData(Context context, String identifer,
				UserData userData) {
			Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
			SharedPreferences sp = context.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			String value = gson.toJson(userData);

			editor.putString(identifer, value);
			editor.commit();
		}

		/**
		 * @param context
		 *            of the running activity.
		 * @param identifier
		 *            is the id used by the system to uniquely identify the user
		 *            within the system.
		 */
		public static void setIdentifier(Context context, String identifier) {
			SharedPreferences sp = context.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			editor.putString("identifier", identifier);
			editor.commit();
		}

		/**
		 * @param context
		 *            of the running activity.
		 */
		public static void clearAllUserData(Context context) {
			SharedPreferences sp = context.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			editor.clear();
			editor.commit();
		}
	}
}
