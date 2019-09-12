package com.peter1303.phonograph.ui.fragments.player;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.peter1303.phonograph.R;
import com.peter1303.phonograph.dialogs.AddToPlaylistDialog;
import com.peter1303.phonograph.dialogs.CreatePlaylistDialog;
import com.peter1303.phonograph.dialogs.SongDetailDialog;
import com.peter1303.phonograph.dialogs.SongShareDialog;
import com.peter1303.phonograph.helper.MusicPlayerRemote;
import com.peter1303.phonograph.interfaces.PaletteColorHolder;
import com.peter1303.phonograph.model.Song;
import com.peter1303.phonograph.ui.activities.tageditor.AbsTagEditorActivity;
import com.peter1303.phonograph.ui.activities.tageditor.SongTagEditorActivity;
import com.peter1303.phonograph.ui.fragments.AbsMusicServiceFragment;
import com.peter1303.phonograph.util.MusicUtil;
import com.peter1303.phonograph.util.NavigationUtil;

import java.util.Objects;

public abstract class AbsPlayerFragment extends AbsMusicServiceFragment implements Toolbar.OnMenuItemClickListener, PaletteColorHolder {

    // TODO 精简菜单

    private Callbacks callbacks;
    private static boolean isToolbarShown = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callbacks = (Callbacks) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement " + Callbacks.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final Song song = MusicPlayerRemote.getCurrentSong();
        switch (item.getItemId()) {
            case R.id.action_toggle_favorite:
                toggleFavorite(song);
                return true;
            case R.id.action_share:
                SongShareDialog.create(song).show(Objects.requireNonNull(getFragmentManager()), "SHARE_SONG");
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(song).show(Objects.requireNonNull(getFragmentManager()), "ADD_PLAYLIST");
                return true;
            case R.id.action_clear_playing_queue:
                MusicPlayerRemote.clearQueue();
                return true;
            case R.id.action_save_playing_queue:
                CreatePlaylistDialog.create(MusicPlayerRemote.getPlayingQueue()).show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "ADD_TO_PLAYLIST");
                return true;
            case R.id.action_tag_editor:
                Intent intent = new Intent(getActivity(), SongTagEditorActivity.class);
                intent.putExtra(AbsTagEditorActivity.EXTRA_ID, song.id);
                startActivity(intent);
                return true;
            case R.id.action_details:
                SongDetailDialog.create(song).show(Objects.requireNonNull(getFragmentManager()), "SONG_DETAIL");
                return true;
            case R.id.action_go_to_album:
                NavigationUtil.goToAlbum(Objects.requireNonNull(getActivity()), song.albumId);
                return true;
            case R.id.action_go_to_artist:
                NavigationUtil.goToArtist(Objects.requireNonNull(getActivity()), song.artistId);
                return true;
        }
        return false;
    }

    protected void toggleFavorite(Song song) {
        MusicUtil.toggleFavorite(Objects.requireNonNull(getActivity()), song);
    }

    private boolean isToolbarShown() {
        return isToolbarShown;
    }

    private void setToolbarShown(boolean toolbarShown) {
        isToolbarShown = toolbarShown;
    }

    private void showToolbar(@Nullable final View toolbar) {
        if (toolbar == null) return;

        setToolbarShown(true);

        toolbar.setVisibility(View.VISIBLE);
        toolbar.animate().alpha(1f).setDuration(PlayerAlbumCoverFragment.VISIBILITY_ANIM_DURATION);
    }

    private void hideToolbar(@Nullable final View toolbar) {
        if (toolbar == null) return;

        setToolbarShown(false);

        toolbar.animate().alpha(0f).setDuration(PlayerAlbumCoverFragment.VISIBILITY_ANIM_DURATION).withEndAction(() -> toolbar.setVisibility(View.GONE));
    }

    protected void toggleToolbar(@Nullable final View toolbar) {
        if (isToolbarShown()) {
            hideToolbar(toolbar);
        } else {
            showToolbar(toolbar);
        }
    }

    protected void checkToggleToolbar(@Nullable final View toolbar) {
        if (toolbar != null && !isToolbarShown() && toolbar.getVisibility() != View.GONE) {
            hideToolbar(toolbar);
        } else if (toolbar != null && isToolbarShown() && toolbar.getVisibility() != View.VISIBLE) {
            showToolbar(toolbar);
        }
    }

    protected String getUpNextAndQueueTime() {
        final long duration = MusicPlayerRemote.getQueueDurationMillis(MusicPlayerRemote.getPosition());

        return MusicUtil.buildInfoString(
            getResources().getString(R.string.up_next),
            MusicUtil.getReadableDurationString(duration)
        );
    }

    public abstract void onShow();

    public abstract void onHide();

    public abstract boolean onBackPressed();

    protected Callbacks getCallbacks() {
        return callbacks;
    }

    public interface Callbacks {
        void onPaletteColorChanged();
    }
}
