package com.peter1303.phonograph.adapter.album;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;
import com.peter1303.phonograph.glide.PhonographColoredTarget;
import com.peter1303.phonograph.glide.SongGlideRequest;
import com.peter1303.phonograph.helper.HorizontalAdapterHelper;
import com.peter1303.phonograph.interfaces.CabHolder;
import com.peter1303.phonograph.model.Album;
import com.peter1303.phonograph.util.MusicUtil;

import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class HorizontalAlbumAdapter extends AlbumAdapter {

    private Context context;

    public HorizontalAlbumAdapter(@NonNull AppCompatActivity activity, List<Album> dataSet, boolean usePalette, @Nullable CabHolder cabHolder) {
        super(activity, dataSet, HorizontalAdapterHelper.LAYOUT_RES, usePalette, cabHolder);
        this.context = activity;
    }

    @Override
    protected ViewHolder createViewHolder(View view, int viewType) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        HorizontalAdapterHelper.applyMarginToLayoutParams(activity, params, viewType);
        return new ViewHolder(view);
    }

    @Override
    protected void setColors(int color, ViewHolder holder) {
        if (holder.itemView != null) {
            CardView card=(CardView)holder.itemView;
            card.setCardBackgroundColor(color);
            if (holder.title != null) {
                    holder.title.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity, ColorUtil.isColorLight(color)));
            }
            if (holder.text != null) {
                    holder.text.setTextColor(MaterialValueHelper.getSecondaryTextColor(activity, ColorUtil.isColorLight(color)));
            }
        }
    }

    @Override
    protected void loadAlbumCover(Album album, final ViewHolder holder) {
        if (holder.image == null) return;
        SongGlideRequest.Builder.from(Glide.with(activity), album.safeGetFirstSong())
                .checkIgnoreMediaStore(activity)
                .generatePalette(activity).build()
                .into(new PhonographColoredTarget(holder.image) {
                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        super.onLoadCleared(placeholder);
                        setColors(getAlbumArtistFooterColor(), holder);
                    }

                    @Override
                    public void onColorReady(int color) {
                        if (usePalette) {
                            setColors(color, holder);
                        } else {
                            setColors(getAlbumArtistFooterColor(), holder);
                        }
                    }
                });
    }

    @Override
    protected String getAlbumText(Album album) {
        return MusicUtil.getYearString(album.getYear());
    }

    @Override
    public int getItemViewType(int position) {
        return HorizontalAdapterHelper.getItemViewtype(position, getItemCount());
    }
}
