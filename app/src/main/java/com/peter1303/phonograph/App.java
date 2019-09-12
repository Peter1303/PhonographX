package com.peter1303.phonograph;

import android.app.Application;
import android.os.Build;
import android.provider.Settings;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.peter1303.phonograph.appshortcuts.DynamicShortcutManager;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class App extends Application {

    private static App app;

    public static String ANDROID_ID = "";

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        // 默认主题
        if (!ThemeStore.isConfigured(this, 1)) {
            ThemeStore.editTheme(this)
                    .primaryColorRes(R.color.md_red_500)
                    .accentColorRes(R.color.md_pink_A400)
                    .commit();
        }
        // 设置 dynamic shortcuts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            new DynamicShortcutManager(this).initDynamicShortcuts();
        }
        ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
    }

    public static App getInstance() {
        return app;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
