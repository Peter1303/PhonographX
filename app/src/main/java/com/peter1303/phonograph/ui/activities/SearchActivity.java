package com.peter1303.phonograph.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.adapter.SearchAdapter;
import com.peter1303.phonograph.adapter.base.MediaEntryViewHolder;
import com.peter1303.phonograph.helper.MusicPlayerRemote;
import com.peter1303.phonograph.helper.menu.OnlineSongMenuHelper;
import com.peter1303.phonograph.interfaces.LoaderIds;
import com.peter1303.phonograph.loader.AlbumLoader;
import com.peter1303.phonograph.loader.ArtistLoader;
import com.peter1303.phonograph.loader.SongLoader;
import com.peter1303.phonograph.misc.WrappedAsyncTaskLoader;
import com.peter1303.phonograph.ui.activities.base.AbsMusicServiceActivity;
import com.peter1303.phonograph.util.ApiUtils;
import com.peter1303.phonograph.model.Music;
import com.peter1303.phonograph.util.SPUtil;
import com.peter1303.phonograph.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AbsMusicServiceActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<Object>> {

    private Context context = this;

    private static final String SEARCH_API = "https://pdev.top/phonograph/api/music.php";

    public static final String QUERY = "query";
    private static final int LOADER_ID = LoaderIds.SEARCH_ACTIVITY;

    @BindView(R.id.activity_search_switch)
    SwitchCompat switchCompat;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.empty)
    TextView empty;

    SearchView searchView;

    private SPUtil spUtil;

    private SearchAdapter adapter;
    private SearchOnlineAdapter onlineAdapter;
    private String query;

    private static boolean netease = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setDrawUnderStatusBar();
        ButterKnife.bind(this);

        setStatusBarColorAuto();
        setNavigationBarColorAuto();
        setTaskDescriptionColorAuto();

        spUtil = new SPUtil(context);

        netease = spUtil.getBoolean("netease", false);

        // thumb color
        int thumbColor = ThemeStore.accentColor(context);
        // trackColor
        int trackColor = 0xfff1f1f1;
        // set the thumb color
        DrawableCompat.setTintList(switchCompat.getThumbDrawable(), new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        thumbColor,
                        trackColor
                }));
        switchCompat.setChecked(netease);
        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            netease = switchCompat.isChecked();
            spUtil.save("netease", netease);
            initAdapter();
        });

        initAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });

        setUpToolBar();

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(QUERY);
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY, query);
    }

    private void setUpToolBar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAdapter() {
        if (!netease) {
            adapter = new SearchAdapter(this, Collections.emptyList());
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    empty.setVisibility(adapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            onlineAdapter = new SearchOnlineAdapter(Collections.emptyList());
            onlineAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    empty.setVisibility(onlineAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                }
            });
            recyclerView.setAdapter(onlineAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchItem.expandActionView();
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return false;
            }
        });

        searchView.setQuery(query, false);
        searchView.post(() -> searchView.setOnQueryTextListener(SearchActivity.this));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(@NonNull String query) {
        this.query = query;
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onMediaStoreChanged() {
        super.onMediaStoreChanged();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        hideSoftKeyboard();
        if (netease) {
            search(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!netease) {
            search(newText);
        }
        return false;
    }

    private void hideSoftKeyboard() {
        Util.hideSoftKeyboard(SearchActivity.this);
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @NonNull
    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        return new AsyncSearchResultLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        if (!netease) {
            adapter.swapDataSet(data);
        } else {
            onlineAdapter.swapDataSet(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        if (!netease) {
            adapter.swapDataSet(Collections.emptyList());
        } else {
            onlineAdapter.swapDataSet(Collections.emptyList());
        }
    }

    private static class AsyncSearchResultLoader extends WrappedAsyncTaskLoader<List<Object>> {
        private final String query;

        AsyncSearchResultLoader(Context context, String query) {
            super(context);
            this.query = query;
        }

        @Override
        public List<Object> loadInBackground() {
            List<Object> results = new ArrayList<>();
            if (!TextUtils.isEmpty(query)) {
                if (!netease) {
                    if (!TextUtils.isEmpty(query)) {
                        List songs = SongLoader.getSongs(getContext(), query.trim());
                        if (!songs.isEmpty()) {
                            results.add(getContext().getResources().getString(R.string.songs));
                            results.addAll(songs);
                        }
                        List artists = ArtistLoader.getArtists(getContext(), query.trim());
                        if (!artists.isEmpty()) {
                            results.add(getContext().getResources().getString(R.string.artists));
                            results.addAll(artists);
                        }
                        List albums = AlbumLoader.getAlbums(getContext(), query.trim());
                        if (!albums.isEmpty()) {
                            results.add(getContext().getResources().getString(R.string.albums));
                            results.addAll(albums);
                        }
                    }
                } else {
                    Map<String, String> list = new HashMap<>();
                    list.put(ApiUtils.INPUT, query.trim());
                    list.put(ApiUtils.FILTER, ApiUtils.NAME);
                    list.put(ApiUtils.TYPE, ApiUtils.NETEASE);
                    list.put(ApiUtils.PAGE, "1");
                    String result = post(SEARCH_API, list);
                    //Log.i("png", result);
                    Gson gson = new Gson();
                    Music music = gson.fromJson(result, Music.class);
                    results.addAll(music.getData());
                    //Log.i("png", music.toString());
                    //Log.i("png", music.getData().toString());
                }
            }
            return results;
        }
    }

    // 在线搜索
    public class SearchOnlineAdapter extends RecyclerView.Adapter<SearchOnlineAdapter.ViewHolder> {

        private List<Object> dataSet;

        SearchOnlineAdapter(@NonNull List<Object> dataSet) {
            this.dataSet = dataSet;
        }

        public void swapDataSet(@NonNull List<Object> dataSet) {
            this.dataSet = dataSet;
            notifyDataSetChanged();
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false), viewType);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final Music.DataBean music = (Music.DataBean) dataSet.get(position);
            holder.title.setText(music.getTitle());
            holder.text.setText(music.getAuthor());
            Glide.with(context).load(music.getPic()).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public class ViewHolder extends MediaEntryViewHolder {
            public ViewHolder(@NonNull View itemView, int itemViewType) {
                super(itemView);
                itemView.setOnLongClickListener(null);
                itemView.setBackgroundColor(ATHUtil.resolveColor(context, R.attr.cardBackgroundColor));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.setElevation(getResources().getDimensionPixelSize(R.dimen.card_elevation));
                }
                if (shortSeparator != null) {
                    shortSeparator.setVisibility(View.GONE);
                }
                if (menu != null) {
                    menu.setVisibility(View.VISIBLE);
                    menu.setOnClickListener(new OnlineSongMenuHelper.OnClickSongMenu((AbsMusicServiceActivity) context) {
                        @Override
                        public Music.DataBean getMusic() {
                            return (Music.DataBean) dataSet.get(getAdapterPosition());
                        }
                    });
                }
            }

            @Override
            public void onClick(View view) {
                Object item = dataSet.get(getAdapterPosition());
                List<Music.DataBean> playList = new ArrayList<>();
                playList.add((Music.DataBean) item);
                //MusicPlayerRemote.openQueue(playList, 0, true);
            }
        }
    }
}
