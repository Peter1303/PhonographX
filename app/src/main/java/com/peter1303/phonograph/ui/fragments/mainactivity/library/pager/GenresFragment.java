package com.peter1303.phonograph.ui.fragments.mainactivity.library.pager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.peter1303.phonograph.R;
import com.peter1303.phonograph.adapter.GenresAdapter;
import com.peter1303.phonograph.interfaces.LoaderIds;
import com.peter1303.phonograph.loader.GenresLoader;
import com.peter1303.phonograph.misc.WrappedAsyncTaskLoader;
import com.peter1303.phonograph.model.Genres;

import java.util.ArrayList;
import java.util.List;

public class GenresFragment extends AbsLibraryPagerRecyclerViewFragment<GenresAdapter, LinearLayoutManager> implements LoaderManager.LoaderCallbacks<List<Genres>> {

    private static final int LOADER_ID = LoaderIds.GENRES_FRAGMENT;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    protected LinearLayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @NonNull
    @Override
    protected GenresAdapter createAdapter() {
        List<Genres> dataSet = getAdapter() == null ? new ArrayList<>() : getAdapter().getDataSet();
        return new GenresAdapter(getLibraryFragment().getMainActivity(), dataSet, R.layout.item_list_no_image);
    }

    @Override
    protected int getEmptyMessage() {
        return R.string.no_genres;
    }

    @Override
    public void onMediaStoreChanged() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    @NonNull
    public Loader<List<Genres>> onCreateLoader(int id, Bundle args) {
        return new GenresFragment.AsyncGenreLoader(getActivity());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Genres>> loader, List<Genres> data) {
        getAdapter().swapDataSet(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Genres>> loader) {
        getAdapter().swapDataSet(new ArrayList<>());
    }

    private static class AsyncGenreLoader extends WrappedAsyncTaskLoader<List<Genres>> {
        public AsyncGenreLoader(Context context) {
            super(context);
        }

        @Override
        public List<Genres> loadInBackground() {
            return GenresLoader.getAllGenres(getContext());
        }
    }
}
