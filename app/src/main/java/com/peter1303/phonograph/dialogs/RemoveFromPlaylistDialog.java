package com.peter1303.phonograph.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.model.PlaylistSong;
import com.peter1303.phonograph.util.PlaylistsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class RemoveFromPlaylistDialog extends DialogFragment {

    @NonNull
    public static RemoveFromPlaylistDialog create(PlaylistSong song) {
        List<PlaylistSong> list = new ArrayList<>();
        list.add(song);
        return create(list);
    }

    @NonNull
    public static RemoveFromPlaylistDialog create(List<PlaylistSong> songs) {
        RemoveFromPlaylistDialog dialog = new RemoveFromPlaylistDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("songs", new ArrayList<>(songs));
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //noinspection unchecked
        final List<PlaylistSong> songs = getArguments().getParcelableArrayList("songs");
        CharSequence content;
        if (songs.size() > 1) {
            content = Html.fromHtml(getString(R.string.remove_x_songs_from_playlist, songs.size()));
        } else {
            content = Html.fromHtml(getString(R.string.remove_song_x_from_playlist, songs.get(0).title));
        }
        return new MaterialDialog.Builder(getActivity())
                .content(content)
                .positiveText(R.string.remove_action)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog, which) -> {
                    if (getActivity() == null)
                        return;
                    PlaylistsUtil.removeFromPlaylist(getActivity(), songs);
                })
                .build();
    }
}
