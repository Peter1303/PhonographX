package com.peter1303.phonograph.ui.fragments.mainactivity.library.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.adapter.online.OnlineAdapter;
import com.peter1303.phonograph.adapter.online.ShuffleButtonOnlineSongAdapter;
import com.peter1303.phonograph.interfaces.LoaderIds;
import com.peter1303.phonograph.misc.WrappedAsyncTaskLoader;
import com.peter1303.phonograph.model.Online;
import com.peter1303.phonograph.model.OnlineInfo;
import com.peter1303.phonograph.ui.activities.MainActivity;
import com.peter1303.phonograph.ui.activities.base.AbsBaseActivity;
import com.peter1303.phonograph.util.ApiUtils;
import com.peter1303.phonograph.util.PreferenceUtil;
import com.peter1303.phonograph.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineFragment extends AbsLibraryPagerRecyclerViewCustomGridSizeFragment<OnlineAdapter, GridLayoutManager> implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<OnlineInfo>> {
    /*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //将指定资源的xml文件转换成具体的view对象
        View view = inflater.inflate(R.layout.fragment_online,null);

        return view;
    }
    */
    private static final int LOADER_ID = LoaderIds.ONLINE_FRAGMENT;

    private static final String SEARCH_API = "https://pdev.top/phonograph/api/music.php";

    private static final String QUERY = "query";

    private String query;

    private static int page = 1;

    private static boolean loading = false;

    private SearchView searchView;

    private List<OnlineInfo> list = new ArrayList<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // init
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(QUERY);
        }
        getLoaderManager().initLoader(LOADER_ID, null, this);
        RecyclerView recyclerView = getAdapter().getRecyclerView();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //newState 分 0,1,2 三个状态,2是滚动状态,0是停止
                super.onScrollStateChanged(recyclerView, newState);
                //-1 代表顶部,返回 true 表示没到顶,还可以滑
                //1  代表底部,返回 true 表示没到底部,还可以滑
                boolean bottom = recyclerView.canScrollVertically(1);
                if (!bottom && !loading) {
                    loadMore();
                }
                Log.i("Phonograph", String.valueOf(bottom));
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY, query);
    }

    @NonNull
    @Override
    protected GridLayoutManager createLayoutManager() {
        return new GridLayoutManager(getActivity(), getGridSize());
    }

    @NonNull
    @Override
    protected OnlineAdapter createAdapter() {
        int itemLayoutRes = getItemLayoutRes();
        notifyLayoutResChanged(itemLayoutRes);
        boolean usePalette = loadUsePalette();
        List<OnlineInfo> dataSet = getAdapter() == null ? new ArrayList<>() : getAdapter().getDataSet();

        if (getGridSize() <= getMaxGridSizeForList()) {
            return new ShuffleButtonOnlineSongAdapter(
                    getLibraryFragment().getMainActivity(),
                    dataSet,
                    itemLayoutRes,
                    usePalette,
                    getLibraryFragment());
        }
        return new OnlineAdapter(
                getLibraryFragment().getMainActivity(),
                dataSet,
                itemLayoutRes,
                usePalette,
                getLibraryFragment());
    }

    @Override
    protected int getEmptyMessage() {
        return R.string.no_songs;
    }

    @Override
    public void onMediaStoreChanged() {
        super.onMediaStoreChanged();
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected String loadSortOrder() {
        return PreferenceUtil.getInstance(getActivity()).getSongSortOrder();
    }

    @Override
    protected void saveSortOrder(String sortOrder) {
        PreferenceUtil.getInstance(getActivity()).setSongSortOrder(sortOrder);
    }

    @Override
    protected void setSortOrder(String sortOrder) {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected int loadGridSize() {
        return PreferenceUtil.getInstance(getActivity()).getSongGridSize(getActivity());
    }

    @Override
    protected void saveGridSize(int gridSize) {
        PreferenceUtil.getInstance(getActivity()).setSongGridSize(gridSize);
    }

    @Override
    protected int loadGridSizeLand() {
        return PreferenceUtil.getInstance(getActivity()).getSongGridSizeLand(getActivity());
    }

    @Override
    protected void saveGridSizeLand(int gridSize) {
        PreferenceUtil.getInstance(getActivity()).setSongGridSizeLand(gridSize);
    }

    @Override
    public void saveUsePalette(boolean usePalette) {
        PreferenceUtil.getInstance(getActivity()).setSongColoredFooters(usePalette);
    }

    @Override
    public boolean loadUsePalette() {
        return PreferenceUtil.getInstance(getActivity()).songColoredFooters();
    }

    @Override
    public void setUsePalette(boolean usePalette) {
        getAdapter().usePalette(usePalette);
    }

    @Override
    protected void setGridSize(int gridSize) {
        getLayoutManager().setSpanCount(gridSize);
        getAdapter().notifyDataSetChanged();
    }

    // 菜单
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final MenuItem searchItem = menu.findItem(R.id.action_input_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });

        searchView.setQuery(query, false);
        searchView.post(() -> searchView.setOnQueryTextListener(this));
    }

    // 搜索
    private void search(@NonNull String query) {
        this.query = query;
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<List<OnlineInfo>> onCreateLoader(int id, Bundle args) {
        loading = false;
        return new AsyncSearchResultLoader(getContext(), query);
    }

    // 加载结束
    @Override
    public void onLoadFinished(Loader<List<OnlineInfo>> loader, List<OnlineInfo> data) {
        list.addAll(data);
        getAdapter().swapDataSet(list);
        loading = false;
    }

    @Override
    public void onLoaderReset(Loader<List<OnlineInfo>> loader) {
        list.clear();
        getAdapter().swapDataSet(Collections.emptyList());
        loading = false;
    }

    public void loadMore() {
        Log.i("Phonograph", String.valueOf(page));
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private static class AsyncSearchResultLoader extends WrappedAsyncTaskLoader<List<OnlineInfo>> {
        private final String query;

        AsyncSearchResultLoader(Context context, String query) {
            super(context);
            this.query = query;
        }

        @Override
        public List<OnlineInfo> loadInBackground() {
            loading = true;
            List<OnlineInfo> results = new ArrayList<>();
            if (!TextUtils.isEmpty(query)) {
                try {
                    Map<String, String> list = new HashMap<>();
                    list.put(ApiUtils.INPUT, query.trim());
                    list.put(ApiUtils.FILTER, ApiUtils.NAME);
                    list.put(ApiUtils.TYPE, ApiUtils.NETEASE);
                    list.put(ApiUtils.PAGE, String.valueOf(page));
                    String result = AbsBaseActivity.post(SEARCH_API, list);
                    //Log.i("png", result);
                    Gson gson = new Gson();
                    Online music = gson.fromJson(result, Online.class);
                    if (music.getCode() == 404) {
                    } else {
                        results.addAll(music.getData());
                        page ++;
                    }
                    //Log.i("png", music.toString());
                    Log.i("png", music.getData().toString());
                }  catch (Exception e) {
                    Intent intent = new Intent(MainActivity.ACTION_SNACKBAR);
                    intent.putExtra("msg", getContext().getString(R.string.snackbar_empty_result));
                    getContext().sendBroadcast(intent);
                    Log.e("Phonograph", e.toString());
                }
            }
            return results;
        }
    }

    //搜索
    @Override
    public boolean onQueryTextSubmit(String text) {
        try {
            search(text);
        } catch (Exception e) {
            Log.i("Phonograph", e.toString());
        }
        hideSoftKeyboard();
        page = 1;
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    private void hideSoftKeyboard() {
        Util.hideSoftKeyboard(getActivity());
        if (searchView != null) {
            searchView.clearFocus();
        }
    }
}
