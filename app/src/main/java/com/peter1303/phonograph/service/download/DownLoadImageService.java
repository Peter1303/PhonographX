/*
 * Peter1303
 * Copyright (c) 2019.
 */

package com.peter1303.phonograph.service.download;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.peter1303.phonograph.util.SPUtil;

import java.io.File;

public class DownLoadImageService implements Runnable {
    private String url;
    private Context context;
    private ImageDownLoadCallBack callBack;

    public DownLoadImageService(Context context, String url, ImageDownLoadCallBack callBack) {
        this.url = url;
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    public void run() {
        File file = null;
        try {
            file = Glide.with(context)
                    .load(url + (new SPUtil(context).getBoolean("album_quality", false) ? "" : "?param=300x300"))
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                callBack.onDownLoadSuccess(file);
            } else {
                callBack.onDownLoadFailed();
            }
        }
    }
}
