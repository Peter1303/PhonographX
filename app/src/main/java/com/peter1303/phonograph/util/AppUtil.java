package com.peter1303.phonograph.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.peter1303.phonograph.helper.MusicPlayerRemote;
import com.peter1303.phonograph.model.Song;
import com.peter1303.phonograph.ui.activities.MainActivity;
import com.peter1303.phonograph.ui.activities.base.AbsSlidingMusicPanelActivity;

import java.io.File;

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

    public static void changed(Context context) {
        Intent intent = new Intent(AbsSlidingMusicPanelActivity.ACTION_CHANGED);
        context.sendBroadcast(intent);
    }

    public static String getName() {
        String data = MusicPlayerRemote.getCurrentSong().data;
        if (!TextUtils.isEmpty(data)) {
            File songFile = new File(MusicPlayerRemote.getCurrentSong().data);
            String abs_path = songFile.getAbsolutePath();
            String full_name = new File(abs_path).getName();
            return full_name.substring(0, full_name.length() - 4);
        }
        return "";
    }
}
