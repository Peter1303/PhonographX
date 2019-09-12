package com.peter1303.phonograph.glide;

import android.content.Context;

import java.io.InputStream;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;
import com.peter1303.phonograph.glide.artistimage.ArtistImage;
import com.peter1303.phonograph.glide.artistimage.ArtistImageLoader;
import com.peter1303.phonograph.glide.audiocover.AudioFileCover;
import com.peter1303.phonograph.glide.audiocover.AudioFileCoverLoader;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class PhonographGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(AudioFileCover.class, InputStream.class, new AudioFileCoverLoader.Factory());
        glide.register(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory());
    }
}
