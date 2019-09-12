package com.peter1303.phonograph.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.peter1303.phonograph.BuildConfig;

public class PurchaseUtil {
    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static String PRO_VERSION = "pro_version";

    public PurchaseUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }

    @SuppressLint("ApplySharedPref")
    public void setProVersion (boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(PRO_VERSION, value).commit();
    }

    public boolean isProVersion() {
        return BuildConfig.DEBUG || sharedPreferences.getBoolean(PRO_VERSION, false);
    }
}
