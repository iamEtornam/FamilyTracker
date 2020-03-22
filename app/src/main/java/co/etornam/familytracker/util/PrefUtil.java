package co.etornam.familytracker.util;

import android.content.Context;

public class PrefUtil {
	public static String WIDGET_SHAREDP = "widgetshared";
	public static String PROFILE_NAME = "profile_name";
	public static String PROFILE_NUMBER = "profile_number";
	public static String PROFILE_HOME = "profile_home";
	public static String PROFILE_WORK = "profile_work";


	public static void setSharedPProfileForWidget(Context context, String profileName, String profileNumber, String profileHomeAd, String profileWorkAd) {

		context.getSharedPreferences(WIDGET_SHAREDP, Context.MODE_PRIVATE).edit().putString(PROFILE_NAME, profileName).apply();
		context.getSharedPreferences(WIDGET_SHAREDP, Context.MODE_PRIVATE).edit().putString(PROFILE_NUMBER, profileNumber).apply();
		context.getSharedPreferences(WIDGET_SHAREDP, Context.MODE_PRIVATE).edit().putString(PROFILE_HOME, profileHomeAd).apply();
		context.getSharedPreferences(WIDGET_SHAREDP, Context.MODE_PRIVATE).edit().putString(PROFILE_WORK, profileWorkAd).apply();
	}


	public static String getSharedPProfileNameForWidget(Context context) {

		return context.getSharedPreferences(WIDGET_SHAREDP, Context.MODE_PRIVATE).getString(PROFILE_NAME, "Nothing here");
	}


	public static String getSharedPProfileNumberForWidget(Context context) {

		return context.getSharedPreferences(WIDGET_SHAREDP, Context.MODE_PRIVATE).getString(PROFILE_NUMBER, "Nothing here");
	}


	public static String getSharedPProfileHomeForWidget(Context context) {

		return context.getSharedPreferences(WIDGET_SHAREDP, Context.MODE_PRIVATE).getString(PROFILE_HOME, "Nothing here");
	}


	public static String getSharedPProfileWorkForWidget(Context context) {

		return context.getSharedPreferences(WIDGET_SHAREDP, Context.MODE_PRIVATE).getString(PROFILE_WORK, "Nothing here");
	}

}
