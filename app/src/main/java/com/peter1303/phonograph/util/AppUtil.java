package com.peter1303.phonograph.util;

import android.content.Context;
import android.content.Intent;

import com.peter1303.phonograph.ui.activities.MainActivity;

public class AppUtil {
    // 用于发送消息到 Activity
    public static void sendMsg(Context context, Object object) {
        Intent intent = new Intent(MainActivity.ACTION_SNACKBAR);
        if (object instanceof String) {
            intent.putExtra("msg", (String) object);
        } else if (object instanceof Integer) {
            intent.putExtra("msg", context.getString((int) object));
        }
        context.sendBroadcast(intent);
    }
}
