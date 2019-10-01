package com.peter1303.phonograph.adapter.online;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialcab.MaterialCab;
import com.bumptech.glide.Glide;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.adapter.base.AbsMultiSelectAdapter;
import com.peter1303.phonograph.adapter.base.MediaEntryViewHolder;
import com.peter1303.phonograph.glide.OnlineGlideRequest;
import com.peter1303.phonograph.glide.PhonographColoredTarget;
import com.peter1303.phonograph.helper.menu.OnlineMenuHelper;
import com.peter1303.phonograph.interfaces.CabHolder;
import com.peter1303.phonograph.model.online.OnlineInfo;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;
import java.util.Objects;

public class OnlineAdapter extends AbsMultiSelectAdapter<OnlineAdapter.ViewHolder, OnlineInfo> implements MaterialCab.Callback, FastScrollRecyclerView.SectionedAdapter {
    protected final AppCompatActivity activity;
    protected List<OnlineInfo> dataSet;

    protected int itemLayoutRes;

    protected RecyclerView recyclerView;

    protected boolean usePalette;
    protected boolean showSectionName;

    public OnlineAdapter(AppCompatActivity activity, List<OnlineInfo> dataSet, @LayoutRes int itemLayoutRes, boolean usePalette, @Nullable CabHolder cabHolder) {
        this(activity, dataSet, itemLayoutRes, usePalette, cabHolder, true);
    }

    public OnlineAdapter(AppCompatActivity activity, List<OnlineInfo> dataSet, @LayoutRes int itemLayoutRes, boolean usePalette, @Nullable CabHolder cabHolder, boolean showSectionName) {
        super(activity, cabHolder, R.menu.menu_media_selection);
        this.activity = activity;
        this.dataSet = dataSet;
        this.itemLayoutRes = itemLayoutRes;
        this.usePalette = usePalette;
        this.showSectionName = showSectionName;
        setHasStableIds(true);
    }

    public void swapDataSet(List<OnlineInfo> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    public void usePalette(boolean usePalette) {
        this.usePalette = usePalette;
        notifyDataSetChanged();
    }

    public List<OnlineInfo> getDataSet() {
        return dataSet;
    }

    private void setColors(int color, ViewHolder holder) {
        if (holder.paletteColorContainer != null) {
            holder.paletteColorContainer.setBackgroundColor(color);
            if (holder.title != null) {
                holder.title.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity, ColorUtil.isColorLight(color)));
            }
            if (holder.text != null) {
                holder.text.setTextColor(MaterialValueHelper.getSecondaryTextColor(activity, ColorUtil.isColorLight(color)));
            }
        }
    }

    private void loadAlbumCover(OnlineInfo song, final ViewHolder holder) {
        if (holder.image == null) return;
        OnlineGlideRequest.Builder.from(Glide.with(activity), song)
                .generatePalette(activity).build()
                .into(new PhonographColoredTarget(holder.image) {
                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        super.onLoadCleared(placeholder);
                        setColors(getDefaultFooterColor(), holder);
                    }

                    @Override
                    public void onColorReady(int color) {
                        if (usePalette) {
                            setColors(color, holder);
                        } else {
                            setColors(getDefaultFooterColor(), holder);
                        }
                    }
                });
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).getSongid();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    @NonNull
    public OnlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false);
        return createViewHolder(view);
    }

    protected OnlineAdapter.ViewHolder createViewHolder(View view) {
        return new OnlineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OnlineAdapter.ViewHolder holder, int position) {
        final OnlineInfo song = dataSet.get(position);
        boolean isChecked = isChecked(song);
        holder.itemView.setActivated(isChecked);
        if (holder.getAdapterPosition() == getItemCount() - 1) {
            if (holder.shortSeparator != null) {
                holder.shortSeparator.setVisibility(View.GONE);
            }
        } else {
            if (holder.shortSeparator != null) {
                holder.shortSeparator.setVisibility(View.VISIBLE);
            }
        }
        Objects.requireNonNull(holder.title).setText(song.getTitle());
        Objects.requireNonNull(holder.text).setText(song.getAuthor());
        loadAlbumCover(song, holder);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    protected OnlineInfo getIdentifier(int position) {
        return dataSet.get(position);
    }

    @Override
    protected String getName(OnlineInfo song) {
        return song.getTitle();
    }

    @Override
    protected void onMultipleItemAction(@NonNull MenuItem menuItem, @NonNull List<OnlineInfo> selection) {
        OnlineMenuHelper.handleMenuClick(activity, (OnlineInfo) selection, menuItem.getItemId());
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return "";
    }

    public class ViewHolder extends MediaEntryViewHolder {
        protected int DEFAULT_MENU_RES = OnlineMenuHelper.MENU_RES;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //setImageTransitionName(activity.getString(R.string.transition_album_art));

            if (menu == null) {
                return;
            }
            menu.setOnClickListener(new OnlineMenuHelper.OnClickSongMenu(activity) {
                @Override
                public OnlineInfo getSong() {
                    return OnlineAdapter.ViewHolder.this.getSong();
                }

                @Override
                public int getMenuRes() {
                    return getSongMenuRes();
                }

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onSongMenuItemClick(item) || super.onMenuItemClick(item);
                }
            });
        }

        protected OnlineInfo getSong() {
            return dataSet.get(getAdapterPosition());
        }

        protected int getSongMenuRes() {
            return DEFAULT_MENU_RES;
        }

        protected boolean onSongMenuItemClick(MenuItem item) {
            if (image != null && image.getVisibility() == View.VISIBLE) {
                switch (item.getItemId()) {
                    /*
                    case R.id.action_go_to_album:
                        Pair[] albumPairs = new Pair[]{
                                Pair.create(image, activity.getResources().getString(R.string.transition_album_art))
                        };
                        NavigationUtil.goToAlbum(activity, getSong().albumId, albumPairs);
                        return true;

                     */
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (isInQuickSelectMode()) {
                toggleChecked(getAdapterPosition());
            } else {
                //MusicPlayerRemote.openQueue(dataSet, getAdapterPosition(), true);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return toggleChecked(getAdapterPosition());
        }
    }
}
