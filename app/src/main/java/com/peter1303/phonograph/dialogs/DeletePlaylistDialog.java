package com.peter1303.phonograph.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.model.Playlist;
import com.peter1303.phonograph.util.PlaylistsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class DeletePlaylistDialog extends DialogFragment {

    @NonNull
    public static DeletePlaylistDialog create(Playlist playlist) {
        List<Playlist> list = new ArrayList<>();
        list.add(playlist);
        return create(list);
    }

    @NonNull
    public static DeletePlaylistDialog create(List<Playlist> playlists) {
        DeletePlaylistDialog dialog = new DeletePlaylistDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("playlists", new ArrayList<>(playlists));
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //noinspection unchecked
        final List<Playlist> playlists = getArguments().getParcelableArrayList("playlists");
        CharSequence content;
        //noinspection ConstantConditions
        if (playlists.size() > 1) {
            content = Html.fromHtml(getString(R.string.delete_x_playlists, playlists.size()));
        } else {
            content = Html.fromHtml(getString(R.string.delete_playlist_x, playlists.get(0).name));
        }
        return new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .content(content)
                .positiveText(R.string.delete_action)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog, which) -> {
                    if (getActivity() == null)
                        return;
                    PlaylistsUtil.deletePlaylists(getActivity(), playlists);
                })
                .build();
    }
}
