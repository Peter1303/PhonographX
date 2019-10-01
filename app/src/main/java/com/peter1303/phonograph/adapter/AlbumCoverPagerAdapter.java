package com.peter1303.phonograph.adapter;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.glide.LocalAlbumGlideRequest;
import com.peter1303.phonograph.glide.PhonographColoredTarget;
import com.peter1303.phonograph.glide.SongGlideRequest;
import com.peter1303.phonograph.misc.CustomFragmentStatePagerAdapter;
import com.peter1303.phonograph.model.Song;
import com.peter1303.phonograph.ui.fragments.player.PlayerAlbumCoverFragment;
import com.peter1303.phonograph.util.AppUtil;
import com.peter1303.phonograph.util.FileUtil;
import com.peter1303.phonograph.util.PreferenceUtil;
import com.peter1303.phonograph.util.SPUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AlbumCoverPagerAdapter extends CustomFragmentStatePagerAdapter {

    private List<Song> dataSet;

    private AlbumCoverFragment.ColorReceiver currentColorReceiver;
    private int currentColorReceiverPosition = -1;

    private ImageView this_albumCover;

    public AlbumCoverPagerAdapter(FragmentManager fm, List<Song> dataSet) {
        super(fm);
        this.dataSet = dataSet;
    }

    @Override
    public Fragment getItem(final int position) {
        return AlbumCoverFragment.newInstance(dataSet.get(position));
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        Object o = super.instantiateItem(container, position);
        if (currentColorReceiver != null && currentColorReceiverPosition == position) {
            receiveColor(currentColorReceiver, currentColorReceiverPosition);
        }
        return o;
    }

    /**
     * Only the latest passed {@link AlbumCoverFragment.ColorReceiver} is guaranteed to receive a response
     */
    public void receiveColor(AlbumCoverFragment.ColorReceiver colorReceiver, int position) {
        AlbumCoverFragment fragment = (AlbumCoverFragment) getFragment(position);
        if (fragment != null) {
            currentColorReceiver = null;
            currentColorReceiverPosition = -1;
            fragment.receiveColor(colorReceiver, position);
            this_albumCover = fragment.getAlbumCover();
        } else {
            currentColorReceiver = colorReceiver;
            currentColorReceiverPosition = position;
        }
    }

    public ImageView getAlbumCover() {
        return this_albumCover;
    }

    public static class AlbumCoverFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final String SONG_ARG = "song";

        private Unbinder unbinder;

        @BindView(R.id.player_image)
        ImageView albumCover;

        private boolean isColorReady;
        private int color;
        private Song song;
        private ColorReceiver colorReceiver;
        private int request;

        static AlbumCoverFragment newInstance(final Song song) {
            AlbumCoverFragment frag = new AlbumCoverFragment();
            final Bundle args = new Bundle();
            args.putParcelable(SONG_ARG, song);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            song = getArguments().getParcelable(SONG_ARG);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_album_cover, container, false);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            forceSquareAlbumCover(false);
            // TODO
//            forceSquareAlbumCover(PreferenceUtil.getInstance(getContext()).forceSquareAlbumCover());
            PreferenceUtil.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
            loadAlbumCover();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            PreferenceUtil.getInstance(getActivity()).unregisterOnSharedPreferenceChangedListener(this);
            unbinder.unbind();
            colorReceiver = null;
        }

        public ImageView getAlbumCover() {
            return albumCover;
        }

        private void loadAlbumCover() {
            // TODO 完善本地加载
            if (new SPUtil(getContext()).getBoolean("online_album", false) &&
                    FileUtil.albumExists(getContext(), AppUtil.getName())) {
                LocalAlbumGlideRequest.Builder.from(getContext(), Glide.with(getActivity()))
                        .generatePalette(getActivity()).build()
                        .into(new PhonographColoredTarget(albumCover) {
                            @Override
                            public void onColorReady(int color) {
                                setColor(color);
                            }
                        });
                /*
                Glide.with(getContext())
                        .load(FileUtil.getAlbumCover(getContext(), AppUtil.getName()))
                        .into(albumCover);
                */
            } else {
                SongGlideRequest.Builder.from(Glide.with(getActivity()), song)
                        .checkIgnoreMediaStore(getActivity())
                        .generatePalette(getActivity()).build()
                        .into(new PhonographColoredTarget(albumCover) {
                            @Override
                            public void onColorReady(int color) {
                                setColor(color);
                            }
                        });
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PreferenceUtil.FORCE_SQUARE_ALBUM_COVER:
                    // TODO
//                    forceSquareAlbumCover(PreferenceUtil.getInstance(getActivity()).forceSquareAlbumCover());
                    break;
            }
        }

        public void forceSquareAlbumCover(boolean forceSquareAlbumCover) {
            albumCover.setScaleType(forceSquareAlbumCover ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.CENTER_CROP);
        }

        private void setColor(int color) {
            this.color = color;
            isColorReady = true;
            if (colorReceiver != null) {
                colorReceiver.onColorReady(color, request);
                colorReceiver = null;
            }
        }

        public void receiveColor(ColorReceiver colorReceiver, int request) {
            if (isColorReady) {
                colorReceiver.onColorReady(color, request);
            } else {
                this.colorReceiver = colorReceiver;
                this.request = request;
            }
        }

        public interface ColorReceiver {
            void onColorReady(int color, int request);
        }
    }
}

