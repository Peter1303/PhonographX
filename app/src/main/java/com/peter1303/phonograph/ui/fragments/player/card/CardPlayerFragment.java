package com.peter1303.phonograph.ui.fragments.player.card;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.adapter.base.MediaEntryViewHolder;
import com.peter1303.phonograph.adapter.song.PlayingQueueAdapter;
import com.peter1303.phonograph.dialogs.LyricsDialog;
import com.peter1303.phonograph.dialogs.SongShareDialog;
import com.peter1303.phonograph.helper.MusicPlayerRemote;
import com.peter1303.phonograph.helper.menu.SongMenuHelper;
import com.peter1303.phonograph.model.Song;
import com.peter1303.phonograph.model.lyrics.Lyrics;
import com.peter1303.phonograph.model.online.lyric.OlLyric;
import com.peter1303.phonograph.model.online.lyric.OlLyricInfo;
import com.peter1303.phonograph.service.download.DownLoadImageService;
import com.peter1303.phonograph.service.download.ImageDownLoadCallBack;
import com.peter1303.phonograph.ui.activities.base.AbsBaseActivity;
import com.peter1303.phonograph.ui.activities.base.AbsSlidingMusicPanelActivity;
import com.peter1303.phonograph.ui.fragments.player.AbsPlayerFragment;
import com.peter1303.phonograph.ui.fragments.player.PlayerAlbumCoverFragment;
import com.peter1303.phonograph.util.ApiUtils;
import com.peter1303.phonograph.util.AppUtil;
import com.peter1303.phonograph.util.FileUtil;
import com.peter1303.phonograph.util.ImageUtil;
import com.peter1303.phonograph.util.MusicUtil;
import com.peter1303.phonograph.util.PurchaseUtil;
import com.peter1303.phonograph.util.SPUtil;
import com.peter1303.phonograph.util.Util;
import com.peter1303.phonograph.util.ViewUtil;
import com.peter1303.phonograph.views.WidthFitSquareLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CardPlayerFragment extends AbsPlayerFragment implements PlayerAlbumCoverFragment.Callbacks, SlidingUpPanelLayout.PanelSlideListener {

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    @BindView(R.id.player_toolbar)
    Toolbar toolbar;
    @BindView(R.id.player_sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @BindView(R.id.player_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.playing_queue_card)
    CardView playingQueueCard;
    @BindView(R.id.color_background)
    View colorBackground;
    @BindView(R.id.player_queue_sub_header)
    TextView playerQueueSubHeader;

    private int lastColor;

    private CardPlayerPlaybackControlsFragment playbackControlsFragment;
    private PlayerAlbumCoverFragment playerAlbumCoverFragment;

    private LinearLayoutManager layoutManager;

    private PlayingQueueAdapter playingQueueAdapter;

    private RecyclerView.Adapter wrappedAdapter;
    private RecyclerViewDragDropManager recyclerViewDragDropManager;

    private AsyncTask updateIsFavoriteTask;
    private AsyncTask updateLyricsAsyncTask;

    private Lyrics lyrics;

    private String title = "";
    private String article = "";

    private Impl impl;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Util.isLandscape(getResources())) {
            impl = new LandscapeImpl(this);
        } else {
            impl = new PortraitImpl(this);
        }

        View view = inflater.inflate(R.layout.fragment_card_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("PrivateResource")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        impl.init();

        setUpPlayerToolbar();
        setUpSubFragments();

        setUpRecyclerView();

        slidingUpPanelLayout.addPanelSlideListener(this);
        slidingUpPanelLayout.setAntiDragView(view.findViewById(R.id.draggable_area));

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                impl.setUpPanelAndAlbumCoverHeight();
            }
        });

        // for some reason the xml attribute doesn't get applied here.
        playingQueueCard.setCardBackgroundColor(ATHUtil.resolveColor(Objects.requireNonNull(getActivity()), R.attr.cardBackgroundColor));
    }

    @Override
    public void onDestroyView() {
        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.removePanelSlideListener(this);
        }
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager.release();
            recyclerViewDragDropManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        playingQueueAdapter = null;
        layoutManager = null;
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager.cancelDrag();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkToggleToolbar(toolbarContainer);
    }

    @Override
    public void onServiceConnected() {
        updateQueue();
        updateCurrentSong();
        updateIsFavorite();
        updateLyrics();
    }

    @Override
    public void onPlayingMetaChanged() {
        updateCurrentSong();
        updateIsFavorite();
        updateQueuePosition();
        updateLyrics();
    }

    @Override
    public void onQueueChanged() {
        updateQueue();
    }

    @Override
    public void onMediaStoreChanged() {
        updateQueue();
    }

    private void updateQueue() {
        playingQueueAdapter.swapDataSet(MusicPlayerRemote.getPlayingQueue(), MusicPlayerRemote.getPosition());
        playerQueueSubHeader.setText(getUpNextAndQueueTime());
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            resetToCurrentPosition();
        }
    }

    private void updateQueuePosition() {
        playingQueueAdapter.setCurrent(MusicPlayerRemote.getPosition());
        playerQueueSubHeader.setText(getUpNextAndQueueTime());
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            resetToCurrentPosition();
        }
    }

    private void updateCurrentSong() {
        impl.updateCurrentSong(MusicPlayerRemote.getCurrentSong());
        title = MusicPlayerRemote.getCurrentSong().title;
        article = MusicPlayerRemote.getCurrentSong().artistName;
    }

    private void setUpSubFragments() {
        playbackControlsFragment = (CardPlayerPlaybackControlsFragment) getChildFragmentManager().findFragmentById(R.id.playback_controls_fragment);
        playerAlbumCoverFragment = (PlayerAlbumCoverFragment) getChildFragmentManager().findFragmentById(R.id.player_album_cover_fragment);

        playerAlbumCoverFragment.setCallbacks(this);
    }

    private void setUpPlayerToolbar() {
        toolbar.inflateMenu(R.menu.menu_player);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
        toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_show_lyrics) {
            if (lyrics != null)
                LyricsDialog.create(lyrics).show(Objects.requireNonNull(getFragmentManager()), "LYRICS");
            return true;
        }
        return super.onMenuItemClick(item);
    }

    private void setUpRecyclerView() {
        recyclerViewDragDropManager = new RecyclerViewDragDropManager();
        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        playingQueueAdapter = new PlayingQueueAdapter(
                ((AppCompatActivity) getActivity()),
                MusicPlayerRemote.getPlayingQueue(),
                MusicPlayerRemote.getPosition(),
                R.layout.item_list,
                false,
                null);
        wrappedAdapter = recyclerViewDragDropManager.createWrappedAdapter(playingQueueAdapter);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wrappedAdapter);
        recyclerView.setItemAnimator(animator);

        recyclerViewDragDropManager.attachRecyclerView(recyclerView);

        layoutManager.scrollToPositionWithOffset(MusicPlayerRemote.getPosition() + 1, 0);
    }

    @SuppressLint("StaticFieldLeak")
    private void updateIsFavorite() {
        if (updateIsFavoriteTask != null) updateIsFavoriteTask.cancel(false);
        updateIsFavoriteTask = new AsyncTask<Song, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Song... params) {
                Activity activity = getActivity();
                if (activity != null) {
                    return MusicUtil.isFavorite(getActivity(), params[0]);
                } else {
                    cancel(false);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                Activity activity = getActivity();
                if (activity != null) {
                    int res = isFavorite ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp;
                    int color = ToolbarContentTintHelper.toolbarContentColor(activity, Color.TRANSPARENT);
                    Drawable drawable = ImageUtil.getTintedVectorDrawable(activity, res, color);
                    toolbar.getMenu().findItem(R.id.action_toggle_favorite)
                            .setIcon(drawable)
                            .setTitle(isFavorite ? getString(R.string.action_remove_from_favorites) : getString(R.string.action_add_to_favorites));
                }
            }
        }.execute(MusicPlayerRemote.getCurrentSong());
    }

    // TODO 可修改为不一样的歌词外观
    @SuppressLint("StaticFieldLeak")
    private void updateLyrics() {
        if (updateLyricsAsyncTask != null) updateLyricsAsyncTask.cancel(false);
        final Song song = MusicPlayerRemote.getCurrentSong();
        updateLyricsAsyncTask = new AsyncTask<Void, Void, Lyrics>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                lyrics = null;
                playerAlbumCoverFragment.setLyrics(null);
                toolbar.getMenu().removeItem(R.id.action_show_lyrics);
                if (new PurchaseUtil(getContext()).isProVersion()) {
                    handler.sendEmptyMessage(2);
                }
            }

            @Override
            protected Lyrics doInBackground(Void... params) {
                String data = MusicUtil.getLyrics(song);
                if (TextUtils.isEmpty(data)) {
                    if (!new PurchaseUtil(Objects.requireNonNull(getContext())).isProVersion()) {
                        return null;
                    }
                    try {
                        Map<String, String> list = new HashMap<>();
                        list.put(ApiUtils.MODE, ApiUtils.LYRIC);
                        String tmp_article = article;
                        String[] replaced = {"/", "&", "\\"};
                        for (String d : replaced) {
                            if (tmp_article.contains(d)) {
                                tmp_article.replace(d, " ");
                            }
                        }
                        list.put(ApiUtils.INPUT, (title + " " + tmp_article).trim());
                        String result = AbsBaseActivity.post(ApiUtils.SEARCH_API, list);
                        Gson gson = new Gson();
                        OlLyric olLyric = gson.fromJson(result, OlLyric.class);
                        List<OlLyricInfo> olLyricInfo = olLyric.getData();
                        if (olLyric.getCode() == 200 && olLyricInfo.size() != 0) {
                            String temp = olLyricInfo.get(0).getLrc();
                            Log.i("Phonograph", "olLyricInfo.getLrc() -> " + temp);
                            if (TextUtils.isEmpty(temp)) {
                                return null;
                            } else {
                                data = temp;
                                // 保存歌词
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = olLyricInfo.get(0);
                                handler.sendMessage(msg);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Phonograph", e.toString());
                    }
                }
                return Lyrics.parse(song, data);
            }

            @Override
            protected void onPostExecute(Lyrics l) {
                lyrics = l;
                playerAlbumCoverFragment.setLyrics(lyrics);
                if (lyrics == null) {
                    if (toolbar != null) {
                        toolbar.getMenu().removeItem(R.id.action_show_lyrics);
                    }
                } else {
                    Activity activity = getActivity();
                    if (toolbar != null && activity != null)
                        if (toolbar.getMenu().findItem(R.id.action_show_lyrics) == null) {
                            int color = ToolbarContentTintHelper.toolbarContentColor(activity, Color.TRANSPARENT);
                            Drawable drawable = ImageUtil.getTintedVectorDrawable(activity, R.drawable.ic_comment_text_outline_white_24dp, color);
                            MenuItemCompat.setShowAsAction(toolbar.getMenu()
                                    .add(Menu.NONE, R.id.action_show_lyrics, Menu.NONE, R.string.action_show_lyrics)
                                    .setIcon(drawable), MenuItem.SHOW_AS_ACTION_IF_ROOM);
                        }
                }
            }

            @Override
            protected void onCancelled(Lyrics s) {
                onPostExecute(null);
            }
        }.execute();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            /*
            List<Song> songs = AlbumLoader.getAlbum(Objects.requireNonNull(getContext()), getId()).songs;
            List<String> paths = new ArrayList<>(songs.size());
            for (Song song : songs) {
                paths.add(song.data);
            }
            Map<FieldKey, String> fieldKeyValueMap = new EnumMap<>(FieldKey.class);
            fieldKeyValueMap.put(FieldKey.ALBUM, "ALBUM");
            fieldKeyValueMap.put(FieldKey.ARTIST, "ARTIST");
            fieldKeyValueMap.put(FieldKey.ALBUM_ARTIST, "ALBUM_ARTIST");
            fieldKeyValueMap.put(FieldKey.GENRE, "1");
            fieldKeyValueMap.put(FieldKey.YEAR, "0");
            AbsTagEditorActivity.context = getContext();
            new AbsTagEditorActivity.WriteTagsAsyncTask().execute(
                    new AbsTagEditorActivity.WriteTagsAsyncTask.LoadingInfo(paths, fieldKeyValueMap, null));
             */
            // TODO 自动保存到歌曲文件
            if (msg.what == 1) {
                OlLyricInfo olLyricInfo = (OlLyricInfo) msg.obj;
                Song temp = MusicPlayerRemote.getCurrentSong();
                String lyric = olLyricInfo.getLrc();
                if (temp != null) {
                    final File songFile = new File(temp.data);
                    String abs_path = songFile.getAbsolutePath();
                    Log.i("Phonograph", "getAbsolutePath -> " + abs_path);
                    String path = new File(abs_path).getParent() + "/";
                    Log.i("Phonograph", "getParent -> " + path);
                    String full_name = new File(abs_path).getName();
                    Log.i("Phonograph", "getName -> " + full_name);
                    String name = full_name.substring(0, full_name.length() - 4);
                    Log.i("Phonograph", "name -> " + name);
                    boolean succeed = FileUtil.string2File(lyric, name, "lrc", path);
                    Log.i("Phonograph", "save -> " + succeed);
                    ImageView albumCover = PlayerAlbumCoverFragment.albumCover;
                    if (albumCover != null && new PurchaseUtil(getContext()).isProVersion()) {
                        if (!olLyricInfo.getPic().isEmpty()) {
                            Glide.with(getContext())
                                    .load(olLyricInfo.getPic())
                                    .into(albumCover);
                            onDownLoad(olLyricInfo.getPic(), name);
                        }
                    }
                    Log.i("Phonograph", "albumCover != null -> " + (albumCover != null));
                }
            } else if (msg.what == 2 && new SPUtil(getContext()).getBoolean("online_album", false)) {
                // 加载本地封面图片
                // TODO 完善本地加载
                ImageView albumCover = PlayerAlbumCoverFragment.albumCover;
                if (albumCover != null && FileUtil.albumExists(getContext(), AppUtil.getName())) {
                    Glide.with(getContext()).load(FileUtil.getAlbumCover(getContext(), AppUtil.getName())).into(albumCover);
                }
                AppUtil.changed(getContext());
            }
        }
    };

    /**
     * 单线程列队执行
     */
    private static ExecutorService singleExecutor = null;

    /**
     * 执行单线程列队执行
     */
    private void runOnQueue(Runnable runnable) {
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

    /**
     * 启动图片下载线程
     */
    private void onDownLoad(String url, String name) {
        DownLoadImageService service = new DownLoadImageService(getContext(), url,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(File file) {
                        // 图片保存
                        Log.i("Phonograph", "onDownLoad: onDownLoadSuccess -> " + file.toString());
                        boolean succeed = FileUtil.album2File(getContext(), file, name);
                        Log.i("Phonograph", "saveAlbumCover -> " + succeed);
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        AppUtil.sendMsg(getContext(), R.string.snackbar_save_album_failed);
                    }
                });
        //启动图片下载线程
        runOnQueue(service);
    }

    @Override
    @ColorInt
    public int getPaletteColor() {
        return lastColor;
    }

    private void animateColorChange(final int newColor) {
        impl.animateColorChange(newColor);
        lastColor = newColor;
    }

    @Override
    protected void toggleFavorite(Song song) {
        super.toggleFavorite(song);
        if (song.id == MusicPlayerRemote.getCurrentSong().id) {
            if (MusicUtil.isFavorite(Objects.requireNonNull(getActivity()), song)) {
                playerAlbumCoverFragment.showHeartAnimation();
            }
            updateIsFavorite();
        }
    }

    @Override
    public void onShow() {
        playbackControlsFragment.show();
    }

    @Override
    public void onHide() {
        playbackControlsFragment.hide();
        onBackPressed();
    }

    @Override
    public boolean onBackPressed() {
        boolean wasExpanded = false;
        if (slidingUpPanelLayout != null) {
            wasExpanded = slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED;
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        return wasExpanded;
    }

    @Override
    public void onColorChanged(int color) {
        animateColorChange(color);
        playbackControlsFragment.setDark(ColorUtil.isColorLight(color));
        getCallbacks().onPaletteColorChanged();
    }

    @Override
    public void onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.getCurrentSong());
    }

    @Override
    public void onToolbarToggled() {
        toggleToolbar(toolbarContainer);
    }

    @Override
    public void onPanelSlide(View view, float slide) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float density = getResources().getDisplayMetrics().density;
            playingQueueCard.setCardElevation((6 * slide + 2) * density);
            playbackControlsFragment.playPauseFab.setElevation((2 * Math.max(0, (1 - (slide * 16))) + 2) * density);
        }
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        switch (newState) {
            case COLLAPSED:
                onPanelCollapsed(panel);
                break;
            case ANCHORED:
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); // this fixes a bug where the panel would get stuck for some reason
                break;
        }
    }

    private void onPanelCollapsed(View panel) {
        resetToCurrentPosition();
    }

    private void resetToCurrentPosition() {
        recyclerView.stopScroll();
        layoutManager.scrollToPositionWithOffset(MusicPlayerRemote.getPosition() + 1, 0);
    }

    interface Impl {
        void init();

        void updateCurrentSong(Song song);

        void animateColorChange(final int newColor);

        void setUpPanelAndAlbumCoverHeight();
    }

    private static abstract class BaseImpl implements Impl {
        protected CardPlayerFragment fragment;

        BaseImpl(CardPlayerFragment fragment) {
            this.fragment = fragment;
        }

        AnimatorSet createDefaultColorChangeAnimatorSet(int newColor) {
            Animator backgroundAnimator;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //noinspection ConstantConditions
                int x = (int) (fragment.playbackControlsFragment.playPauseFab.getX() + fragment.playbackControlsFragment.playPauseFab.getWidth() / 2 + fragment.playbackControlsFragment.getView().getX());
                int y = (int) (fragment.playbackControlsFragment.playPauseFab.getY() + fragment.playbackControlsFragment.playPauseFab.getHeight() / 2 + fragment.playbackControlsFragment.getView().getY() + fragment.playbackControlsFragment.progressSlider.getHeight());
                float startRadius = Math.max(fragment.playbackControlsFragment.playPauseFab.getWidth() / 2, fragment.playbackControlsFragment.playPauseFab.getHeight() / 2);
                float endRadius = Math.max(fragment.colorBackground.getWidth(), fragment.colorBackground.getHeight());
                fragment.colorBackground.setBackgroundColor(newColor);
                backgroundAnimator = ViewAnimationUtils.createCircularReveal(fragment.colorBackground, x, y, startRadius, endRadius);
            } else {
                backgroundAnimator = ViewUtil.createBackgroundColorTransition(fragment.colorBackground, fragment.lastColor, newColor);
            }

            AnimatorSet animatorSet = new AnimatorSet();

            animatorSet.play(backgroundAnimator);

            if (!ATHUtil.isWindowBackgroundDark(Objects.requireNonNull(fragment.getActivity()))) {
                int adjustedLastColor = ColorUtil.isColorLight(fragment.lastColor) ? ColorUtil.darkenColor(fragment.lastColor) : fragment.lastColor;
                int adjustedNewColor = ColorUtil.isColorLight(newColor) ? ColorUtil.darkenColor(newColor) : newColor;
                Animator subHeaderAnimator = ViewUtil.createTextColorTransition(fragment.playerQueueSubHeader, adjustedLastColor, adjustedNewColor);
                animatorSet.play(subHeaderAnimator);
            }

            animatorSet.setDuration(ViewUtil.PHONOGRAPH_ANIM_TIME);
            return animatorSet;
        }

        @Override
        public void animateColorChange(int newColor) {
            if (ATHUtil.isWindowBackgroundDark(Objects.requireNonNull(fragment.getActivity()))) {
                fragment.playerQueueSubHeader.setTextColor(ThemeStore.textColorSecondary(fragment.getActivity()));
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static class PortraitImpl extends BaseImpl {
        MediaEntryViewHolder currentSongViewHolder;
        Song currentSong = Song.EMPTY_SONG;

        PortraitImpl(CardPlayerFragment fragment) {
            super(fragment);
        }

        @Override
        public void init() {
            currentSongViewHolder = new MediaEntryViewHolder(fragment.getView().findViewById(R.id.current_song));

            currentSongViewHolder.separator.setVisibility(View.VISIBLE);
            currentSongViewHolder.shortSeparator.setVisibility(View.GONE);
            currentSongViewHolder.image.setScaleType(ImageView.ScaleType.CENTER);
            currentSongViewHolder.image.setColorFilter(ATHUtil.resolveColor(fragment.getActivity(), R.attr.iconColor, ThemeStore.textColorSecondary(fragment.getActivity())), PorterDuff.Mode.SRC_IN);
            currentSongViewHolder.image.setImageResource(R.drawable.ic_volume_up_white_24dp);
            currentSongViewHolder.itemView.setOnClickListener(v -> {
                // toggle the panel
                if (fragment.slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    fragment.slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                } else if (fragment.slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    fragment.slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            });
            currentSongViewHolder.menu.setOnClickListener(new SongMenuHelper.OnClickSongMenu((AppCompatActivity) fragment.getActivity()) {
                @Override
                public Song getSong() {
                    return currentSong;
                }

                public int getMenuRes() {
                    return R.menu.menu_item_playing_queue_song;
                }

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_remove_from_playing_queue:
                            MusicPlayerRemote.removeFromQueue(MusicPlayerRemote.getPosition());
                            return true;
                        case R.id.action_share:
                            SongShareDialog.create(getSong()).show(fragment.getFragmentManager(), "SONG_SHARE_DIALOG");
                            return true;
                    }
                    return super.onMenuItemClick(item);
                }
            });
        }

        @Override
        public void setUpPanelAndAlbumCoverHeight() {
            WidthFitSquareLayout albumCoverContainer = fragment.getView().findViewById(R.id.album_cover_container);

            final int availablePanelHeight = fragment.slidingUpPanelLayout.getHeight() - fragment.getView().findViewById(R.id.player_content).getHeight() + (int) ViewUtil.convertDpToPixel(8, fragment.getResources());
            final int minPanelHeight = (int) ViewUtil.convertDpToPixel(72 + 24, fragment.getResources());
            if (availablePanelHeight < minPanelHeight) {
                albumCoverContainer.getLayoutParams().height = albumCoverContainer.getHeight() - (minPanelHeight - availablePanelHeight);
                albumCoverContainer.forceSquare(false);
            }
            fragment.slidingUpPanelLayout.setPanelHeight(Math.max(minPanelHeight, availablePanelHeight));

            ((AbsSlidingMusicPanelActivity) fragment.getActivity()).setAntiDragView(fragment.slidingUpPanelLayout.findViewById(R.id.player_panel));
        }

        @Override
        public void updateCurrentSong(Song song) {
            currentSong = song;
            currentSongViewHolder.title.setText(song.title);
            currentSongViewHolder.text.setText(MusicUtil.getSongInfoString(song));
        }

        @Override
        public void animateColorChange(int newColor) {
            super.animateColorChange(newColor);

            fragment.slidingUpPanelLayout.setBackgroundColor(fragment.lastColor);

            createDefaultColorChangeAnimatorSet(newColor).start();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static class LandscapeImpl extends BaseImpl {
        LandscapeImpl(CardPlayerFragment fragment) {
            super(fragment);
        }

        @Override
        public void init() {
        }

        @Override
        public void setUpPanelAndAlbumCoverHeight() {
            int panelHeight = fragment.slidingUpPanelLayout.getHeight() - fragment.playbackControlsFragment.getView().getHeight();
            fragment.slidingUpPanelLayout.setPanelHeight(panelHeight);

            ((AbsSlidingMusicPanelActivity) fragment.getActivity()).setAntiDragView(fragment.slidingUpPanelLayout.findViewById(R.id.player_panel));
        }

        @Override
        public void updateCurrentSong(Song song) {
            fragment.toolbar.setTitle(song.title);
            fragment.toolbar.setSubtitle(MusicUtil.getSongInfoString(song));
        }

        @Override
        public void animateColorChange(int newColor) {
            super.animateColorChange(newColor);

            fragment.slidingUpPanelLayout.setBackgroundColor(fragment.lastColor);

            AnimatorSet animatorSet = createDefaultColorChangeAnimatorSet(newColor);
            animatorSet.play(ViewUtil.createBackgroundColorTransition(fragment.toolbar, fragment.lastColor, newColor))
                    .with(ViewUtil.createBackgroundColorTransition(fragment.getView().findViewById(R.id.status_bar), ColorUtil.darkenColor(fragment.lastColor), ColorUtil.darkenColor(newColor)));
            animatorSet.start();
        }
    }
}
