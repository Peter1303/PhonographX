package com.peter1303.phonograph.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SPUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }

    public SPUtil(Context context,String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    @SuppressLint("ApplySharedPref")
    public void save (String tag, boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(tag, value).commit();
    }

    @SuppressLint("ApplySharedPref")
    public void save(String tag, int value) {
        editor = sharedPreferences.edit();
        editor.putInt(tag, value).commit();
    }

    public boolean getBoolean(String tag, boolean d) {
        return sharedPreferences.getBoolean(tag, d);
    }

    public int getInt(String tag, int d) {
        return sharedPreferences.getInt(tag, d);
    }
}
