/*
 * Peter1303
 * Copyright (c) 2019.
 */

package com.peter1303.phonograph.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.glide.palette.BitmapPaletteTranscoder;
import com.peter1303.phonograph.glide.palette.BitmapPaletteWrapper;
import com.peter1303.phonograph.util.AppUtil;
import com.peter1303.phonograph.util.FileUtil;

public class LocalAlbumGlideRequest {
    public static final DiskCacheStrategy DEFAULT_DISK_CACHE_STRATEGY = DiskCacheStrategy.NONE;
    public static final int DEFAULT_ERROR_IMAGE = R.drawable.default_album_art;
    public static final int DEFAULT_ANIMATION = android.R.anim.fade_in;

    public static class Builder {
        final RequestManager requestManager;
        final Context context;

        public static Builder from(Context context, @NonNull RequestManager requestManager) {
            return new Builder(context, requestManager);
        }

        private Builder(Context context, @NonNull RequestManager requestManager) {
            this.context = context;
            this.requestManager = requestManager;
        }

        public PaletteBuilder generatePalette(Context context) {
            return new PaletteBuilder(this, context);
        }

        public BitmapBuilder asBitmap() {
            return new BitmapBuilder(context, this);
        }

        public DrawableRequestBuilder<GlideDrawable> build() {
            //noinspection unchecked
            return createBaseRequest(context, requestManager)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION);
        }
    }

    public static class BitmapBuilder {
        private final Context context;
        private final Builder builder;

        public BitmapBuilder(Context context, Builder builder) {
            this.context = context;
            this.builder = builder;
        }

        public BitmapRequestBuilder<?, Bitmap> build() {
            //noinspection unchecked
            return createBaseRequest(context, builder.requestManager)
                    .asBitmap()
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION);
        }
    }

    public static class PaletteBuilder {
        final Context context;
        private final Builder builder;

        public PaletteBuilder(Builder builder, Context context) {
            this.builder = builder;
            this.context = context;
        }

        public BitmapRequestBuilder<?, BitmapPaletteWrapper> build() {
            //noinspection unchecked
            return createBaseRequest(context, builder.requestManager)
                    .asBitmap()
                    .transcode(new BitmapPaletteTranscoder(context), BitmapPaletteWrapper.class)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION);
        }
    }

    public static DrawableTypeRequest createBaseRequest(Context context, RequestManager requestManager) {
        if (FileUtil.albumExists(context, AppUtil.getName())) {
            // TODO 完善本地加载
            return requestManager.load(FileUtil.getAlbumCover(context, AppUtil.getName()));
        }
        return requestManager.load(R.drawable.default_album_art);
        //return requestManager.loadFromMediaStore(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId));
    }
}
