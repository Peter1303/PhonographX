package com.peter1303.phonograph.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.peter1303.phonograph.R;
import com.peter1303.phonograph.helper.MusicPlayerRemote;
import com.peter1303.phonograph.model.Genres;
import com.peter1303.phonograph.model.Playlist;
import com.peter1303.phonograph.ui.activities.AlbumDetailActivity;
import com.peter1303.phonograph.ui.activities.ArtistDetailActivity;
import com.peter1303.phonograph.ui.activities.EqualizerActivity;
import com.peter1303.phonograph.ui.activities.GenresDetailActivity;
import com.peter1303.phonograph.ui.activities.PlaylistDetailActivity;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class NavigationUtil {

    public static void goToArtist(@NonNull final Activity activity, final int artistId, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_ID, artistId);

        //noinspection unchecked
        if (sharedElements != null && sharedElements.length > 0) {
            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    public static void goToAlbum(@NonNull final Activity activity, final int albumId, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_ID, albumId);

        //noinspection unchecked
        if (sharedElements != null && sharedElements.length > 0) {
            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    public static void goToGenre(@NonNull final Activity activity, final Genres genres, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, GenresDetailActivity.class);
        intent.putExtra(GenresDetailActivity.EXTRA_GENRE, genres);

        activity.startActivity(intent);
    }

    public static void goToPlaylist(@NonNull final Activity activity, final Playlist playlist, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, PlaylistDetailActivity.class);
        intent.putExtra(PlaylistDetailActivity.EXTRA_PLAYLIST, playlist);
        activity.startActivity(intent);
    }

    public static void openEqualizer(@NonNull final Activity activity) {
        final int sessionId = MusicPlayerRemote.getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            AppUtil.sendMsg(activity, R.string.no_audio_ID);
        } else {
            SPUtil sp = new SPUtil(activity);
            if (sp.getBoolean("equalizer_default", false)) {
                try {
                    final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
                    effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                    activity.startActivityForResult(effects, 0);
                } catch (@NonNull final ActivityNotFoundException notFound) {
                    AppUtil.sendMsg(activity, R.string.no_equalizer);
                }
            } else {
                activity.startActivity(new Intent(activity, EqualizerActivity.class));
            }
        }
    }
}
