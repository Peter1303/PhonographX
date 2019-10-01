/*
 * Peter1303
 * Copyright (c) 2019.
 */

package com.peter1303.phonograph.service.download;

import java.io.File;

public interface ImageDownLoadCallBack {

    void onDownLoadSuccess(File file);

    void onDownLoadFailed();
}
