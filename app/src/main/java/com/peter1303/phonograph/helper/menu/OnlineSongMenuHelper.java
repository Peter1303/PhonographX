package com.peter1303.phonograph.helper.menu;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.peter1303.phonograph.R;
import com.peter1303.phonograph.model.Music;

public class OnlineSongMenuHelper {
    public static final int MENU_RES = R.menu.menu_item_online;

    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull Music.DataBean music, int menuItemId) {
        return false;
    }

    public static abstract class OnClickSongMenu implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private AppCompatActivity activity;

        public OnClickSongMenu(@NonNull AppCompatActivity activity) {
            this.activity = activity;
        }

        public int getMenuRes() {
            return MENU_RES;
        }

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(activity, v);
            popupMenu.inflate(getMenuRes());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return handleMenuClick(activity, getMusic(), item.getItemId());
        }

        public abstract Music.DataBean getMusic();
    }
}
