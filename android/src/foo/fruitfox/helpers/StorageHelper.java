package foo.fruitfox.helpers;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
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
			Gson gson = new Gson();
			UserData ud = null;
			SharedPreferences sp = c.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			String value = sp.getString(GUID, null);
			DebugHelper.ShowMessage.d("StorageHelper", value);
			
			if (value != null) {
				ud = gson.fromJson(value.toString(), UserData.class);
			}

			return ud;
		}

		/**
		 * @param c
		 * @param GUID
		 * @param ud
		 */
		public static void setUserData(Context c, String GUID, UserData ud) {
			Gson gson = new Gson();
			SharedPreferences sp = c.getSharedPreferences("UserData",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			String value = gson.toJson(ud);

			editor.putString(GUID, value);
			editor.commit();
		}
	}
}
