package com.peter1303.phonograph.loader;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.peter1303.phonograph.model.Genres;
import com.peter1303.phonograph.model.Song;
import com.peter1303.phonograph.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class GenresLoader {

    @NonNull
    public static List<Genres> getAllGenres(@NonNull final Context context) {
        return getGenresFromCursor(context, makeGenreCursor(context));
    }

    @NonNull
    public static List<Song> getSongs(@NonNull final Context context, final int genreId) {
        return SongLoader.getSongs(makeGenreSongCursor(context, genreId));
    }

    @NonNull
    private static List<Genres> getGenresFromCursor(@NonNull final Context context, @Nullable final Cursor cursor) {
        final List<Genres> genres = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Genres genre = getGenreFromCursor(context, cursor);
                    if (genre.songCount > 0) {
                        genres.add(genre);
                    } else {
                        // try to remove the empty genre from the media store
                        try {
                            context.getContentResolver().delete(android.provider.MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, android.provider.MediaStore.Audio.Genres._ID + " == " + genre.id, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // nothing we can do then
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return genres;
    }

    @NonNull
    private static Genres getGenreFromCursor(@NonNull final Context context, @NonNull final Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        final int songs = getSongs(context, id).size();
        return new Genres(id, name, songs);
    }

    @Nullable
    private static Cursor makeGenreSongCursor(@NonNull final Context context, int genreId) {
        try {
            return context.getContentResolver().query(
                    android.provider.MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                    SongLoader.BASE_PROJECTION, SongLoader.BASE_SELECTION, null, PreferenceUtil.getInstance(context).getSongSortOrder());
        } catch (SecurityException e) {
            return null;
        }
    }

    @Nullable
    private static Cursor makeGenreCursor(@NonNull final Context context) {
        final String[] projection = new String[]{
                android.provider.MediaStore.Audio.Genres._ID,
                android.provider.MediaStore.Audio.Genres.NAME
        };

        try {
            return context.getContentResolver().query(
                    android.provider.MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                    projection, null, null, PreferenceUtil.getInstance(context).getGenreSortOrder());
        } catch (SecurityException e) {
            return null;
        }
    }
}
