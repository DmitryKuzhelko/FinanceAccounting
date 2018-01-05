package com.bignerdranch.android.financeaccounting.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bignerdranch.android.financeaccounting.Constants;

public class PreferencesHelper {

    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    public static void init(Context cntxt) {
        context = cntxt;
    }

    private static void init() {
        settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void addProperty(String name, String value) {
        if (settings == null) {
            init();
        }
        editor.putString(name, value);
        editor.apply();
    }

    public static String getCategory(String name) {
        if (settings == null) {
            init();
        }
        return settings.getString(name, "");//add default category
    }

    public static String getRange(String name) {
        if (settings == null) {
            init();
        }
        return settings.getString(name, Constants.MONTH);
    }

}
