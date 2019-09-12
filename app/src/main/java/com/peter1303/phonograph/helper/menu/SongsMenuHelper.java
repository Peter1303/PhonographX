package com.peter1303.phonograph.helper.menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.peter1303.phonograph.R;
import com.peter1303.phonograph.dialogs.AddToPlaylistDialog;
import com.peter1303.phonograph.dialogs.DeleteSongsDialog;
import com.peter1303.phonograph.helper.MusicPlayerRemote;
import com.peter1303.phonograph.model.Song;

import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class SongsMenuHelper {
    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull List<Song> songs, int menuItemId) {
        switch (menuItemId) {
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(songs).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(songs).show(activity.getSupportFragmentManager(), "DELETE_SONGS");
                return true;
        }
        return false;
    }
}
