package com.peter1303.phonograph.adapter.online;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.peter1303.phonograph.R;
import com.peter1303.phonograph.interfaces.CabHolder;
import com.peter1303.phonograph.model.OnlineInfo;

import java.util.List;

public class AbsOffsetOnlineSongAdapter extends OnlineAdapter {

    protected static final int OFFSET_ITEM = 0;
    protected static final int SONG = 1;

    public AbsOffsetOnlineSongAdapter(AppCompatActivity activity, List<OnlineInfo> dataSet, @LayoutRes int itemLayoutRes, boolean usePalette, @Nullable CabHolder cabHolder) {
        super(activity, dataSet, itemLayoutRes, usePalette, cabHolder);
    }

    public AbsOffsetOnlineSongAdapter(AppCompatActivity activity, List<OnlineInfo> dataSet, @LayoutRes int itemLayoutRes, boolean usePalette, @Nullable CabHolder cabHolder, boolean showSectionName) {
        super(activity, dataSet, itemLayoutRes, usePalette, cabHolder, showSectionName);
    }

    @NonNull
    @Override
    public OnlineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == OFFSET_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_list_single_row, parent, false);
            return createViewHolder(view);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected OnlineAdapter.ViewHolder createViewHolder(View view) {
        return new AbsOffsetOnlineSongAdapter.ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        position--;
        if (position < 0) return -2;
        return super.getItemId(position);
    }

    @Nullable
    @Override
    protected OnlineInfo getIdentifier(int position) {
        position--;
        if (position < 0) return null;
        return super.getIdentifier(position);
    }

    @Override
    public int getItemCount() {
        int superItemCount = super.getItemCount();
        return superItemCount == 0 ? 0 : superItemCount + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? OFFSET_ITEM : SONG;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        position--;
        if (position < 0) return "";
        return super.getSectionName(position);
    }

    public class ViewHolder extends OnlineAdapter.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected OnlineInfo getSong() {
            /*
            if (getItemViewType() == OFFSET_ITEM)
                return Song.EMPTY_SONG; // could also return null, just to be safe return empty song
             */
            return dataSet.get(getAdapterPosition() - 1);
        }

        @Override
        public void onClick(View v) {
            if (isInQuickSelectMode() && getItemViewType() != OFFSET_ITEM) {
                toggleChecked(getAdapterPosition());
            } else {
                //MusicPlayerRemote.openQueue(dataSet, getAdapterPosition() - 1, true);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (getItemViewType() == OFFSET_ITEM) return false;
            toggleChecked(getAdapterPosition());
            return true;
        }
    }
}