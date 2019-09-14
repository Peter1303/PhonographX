package com.peter1303.phonograph.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.Equalizer;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.snackbar.Snackbar;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.color.MaterialColor;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.helper.MusicPlayerRemote;
import com.peter1303.phonograph.service.MusicService;
import com.peter1303.phonograph.ui.activities.base.AbsBaseActivity;
import com.peter1303.phonograph.util.SPUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EqualizerActivity extends AbsBaseActivity {
    private Context context = this;

    @BindView(R.id.activity_equalizer_layout)
    CoordinatorLayout layout;

    @BindView(R.id.equalizer_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_equalizer_title_general)
    TextView title_general;
    @BindView(R.id.activity_equalizer_title_base)
    TextView title_base;
    @BindView(R.id.activity_equalizer_title_special)
    TextView title_special;

    @BindView(R.id.activity_equalizer_switch)
    SwitchCompat switchCompat_switch;
    @BindView(R.id.activity_equalizer_bass_boost_switch)
    SwitchCompat switchCompat_bass_boost;
    @BindView(R.id.activity_equalizer_virtualizer_switch)
    SwitchCompat switchCompat_virtualizer;
    @BindView(R.id.activity_equalizer_acoustic_echo_canceler_switch)
    SwitchCompat switchCompat_acoustic_echo_canceler;
    @BindView(R.id.activity_equalizer_automatic_gain_control_switch)
    SwitchCompat switchCompat_automatic_gain_control;
    @BindView(R.id.activity_equalizer_noise_suppressor_switch)
    SwitchCompat switchCompat_noise_suppressor;

    @BindView(R.id.activity_equalizer_bass_seekbar)
    SeekBar seekBar_bass;
    @BindView(R.id.activity_equalizer_virtualizer_seekbar)
    SeekBar seekBar_virtualizer;

    @BindView(R.id.activity_equalizer_spinner)
    AppCompatSpinner spinner;

    @BindView(R.id.activity_equalizer_seekbar_layout)
    LinearLayout seekBar_layout;

    private SeekBar[] seekBars;

    private Equalizer mEqualizer;

    private MusicService playService;

    private SPUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        setDrawUnderStatusBar();
        ButterKnife.bind(this);

        sp = new SPUtil(context, "audioEffect");

        initViews();
        setUpToolbar();
        setStatusBarColorAuto();
        setNavigationBarColorAuto();
        setTaskDescriptionColorAuto();
        equalizer();
        getPreference();
    }

    private void initViews() {
        int thumbColor = ThemeStore.accentColor(context);
        int trackColor = 0xfff1f1f1;
        DrawableCompat.setTintList(switchCompat_switch.getThumbDrawable(), new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{thumbColor, trackColor}));
        DrawableCompat.setTintList(switchCompat_bass_boost.getThumbDrawable(), new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{thumbColor, trackColor}));
        DrawableCompat.setTintList(switchCompat_virtualizer.getThumbDrawable(), new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{thumbColor, trackColor}));
        DrawableCompat.setTintList(switchCompat_acoustic_echo_canceler.getThumbDrawable(), new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{thumbColor, trackColor}));
        DrawableCompat.setTintList(switchCompat_automatic_gain_control.getThumbDrawable(), new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{thumbColor, trackColor}));
        DrawableCompat.setTintList(switchCompat_noise_suppressor.getThumbDrawable(), new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{thumbColor, trackColor}));
        //LayerDrawable layerDrawable = (LayerDrawable) seekBar_bass.getProgressDrawable();
        //Drawable drawable =layerDrawable.getDrawable(2);
        //drawable.setColorFilter(trackColor, PorterDuff.Mode.SRC);
        seekBar_bass.getThumb().setColorFilter(thumbColor, PorterDuff.Mode.SRC_ATOP);
        seekBar_bass.invalidate();
        seekBar_virtualizer.getThumb().setColorFilter(thumbColor, PorterDuff.Mode.SRC_ATOP);
        seekBar_virtualizer.invalidate();
    }

    private void setUpToolbar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.equalizer));
    }

    @SuppressLint("SetTextI18n")
    private void equalizer() {
        playService = MusicPlayerRemote.getMusicService();
        seekBar_bass.setMax(1000);
        seekBar_virtualizer.setMax(1000);
        //均衡器
        try {
            mEqualizer = new Equalizer(0, playService.getAudioSessionId());
            seekBars = new SeekBar[5];
            switchCompat_switch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                seekBar_layout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                spinner.setEnabled(isChecked);
                playService.setEqualizer(isChecked);
                sp.save("Equalizer", isChecked);
            });
            // 获取均衡控制器支持最小值和最大值
            final short minEQLevel = mEqualizer.getBandLevelRange()[0];//第一个下标为最低的限度范围
            final short maxEQLevel = mEqualizer.getBandLevelRange()[1];  // 第二个下标为最高的限度范围
            // 获取均衡控制器支持的所有频率
            short brands = mEqualizer.getNumberOfBands();
            for (short i = 0; i < brands; i++) {
                TextView eqTextView = new TextView(this);
                // 创建一个TextView，用于显示频率
                eqTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                eqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                // 设置该均衡控制器的频率
                eqTextView.setText((mEqualizer.getCenterFreq(i) / 1000) + " Hz");
                seekBar_layout.addView(eqTextView);
                // 创建一个水平排列组件的LinearLayout
                LinearLayout tmpLayout = new LinearLayout(this);
                tmpLayout.setOrientation(LinearLayout.HORIZONTAL);
                // 创建显示均衡控制器最小值的TextView
                TextView minDbTextView = new TextView(this);
                minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                // 显示均衡控制器的最小值
                minDbTextView.setText((minEQLevel / 100) + " dB");
                // 创建显示均衡控制器最大值的TextView
                TextView maxDbTextView = new TextView(this);
                maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                // 显示均衡控制器的最大值
                maxDbTextView.setText((maxEQLevel / 100) + " dB");
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                SeekBar bar = new SeekBar(this);
                seekBars[i] = bar;
                bar.setLayoutParams(layoutParams);
                bar.setMax(maxEQLevel - minEQLevel);
                final short band = i;
                // 为SeekBar的拖动事件设置事件监听器
                bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Log.v("均衡器改变", "是");
                        if (fromUser) {
                            spinner.setSelection(12);
                        }
                        // 设置该频率的均衡值
                        playService.setEqualizerBandLevel(band, (short) (progress + minEQLevel));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                // 使用水平排列组件的 LinearLayout“盛装”3个组件
                tmpLayout.addView(minDbTextView);
                tmpLayout.addView(bar);
                tmpLayout.addView(maxDbTextView);
                // 将水平排列组件的 LinearLayout 添加到 myLayout 容器中
                seekBar_layout.addView(tmpLayout);
            }
        } catch (Exception e) {
            Log.e("Phonograph", e.toString());
        }
        //音场
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (seekBars != null){
                    sp.save("Spinner", spinner.getSelectedItemPosition());
                    switch (i) {
                        case 0:
                            seekBars[0].setProgress(1800);
                            seekBars[1].setProgress(1500);
                            seekBars[2].setProgress(1500);
                            seekBars[3].setProgress(1500);
                            seekBars[4].setProgress(1800);
                            return;
                        case 1:
                            seekBars[0].setProgress(2000);
                            seekBars[1].setProgress(1800);
                            seekBars[2].setProgress(1300);
                            seekBars[3].setProgress(1900);
                            seekBars[4].setProgress(1900);
                            return;
                        case 2:
                            seekBars[0].setProgress(2100);
                            seekBars[1].setProgress(1500);
                            seekBars[2].setProgress(1700);
                            seekBars[3].setProgress(1900);
                            seekBars[4].setProgress(1600);
                            return;
                        case 3:
                            seekBars[0].setProgress(1500);
                            seekBars[1].setProgress(1500);
                            seekBars[2].setProgress(1500);
                            seekBars[3].setProgress(1500);
                            seekBars[4].setProgress(1500);
                            return;
                        case 4:
                            seekBars[0].setProgress(1800);
                            seekBars[1].setProgress(1500);
                            seekBars[2].setProgress(1500);
                            seekBars[3].setProgress(1700);
                            seekBars[4].setProgress(1400);
                            return;
                        case 5:
                            seekBars[0].setProgress(1900);
                            seekBars[1].setProgress(1600);
                            seekBars[2].setProgress(2600);
                            seekBars[3].setProgress(1800);
                            seekBars[4].setProgress(1500);
                            return;
                        case 6:
                            seekBars[0].setProgress(2000);
                            seekBars[1].setProgress(1800);
                            seekBars[2].setProgress(1500);
                            seekBars[3].setProgress(1600);
                            seekBars[4].setProgress(1800);
                            return;
                        case 7:
                            seekBars[0].setProgress(1900);
                            seekBars[1].setProgress(1700);
                            seekBars[2].setProgress(1300);
                            seekBars[3].setProgress(1700);
                            seekBars[4].setProgress(2000);
                            return;
                        case 8:
                            seekBars[0].setProgress(1400);
                            seekBars[1].setProgress(1700);
                            seekBars[2].setProgress(2000);
                            seekBars[3].setProgress(1600);
                            seekBars[4].setProgress(1300);
                            return;
                        case 9:
                            seekBars[0].setProgress(2000);
                            seekBars[1].setProgress(1800);
                            seekBars[2].setProgress(1400);
                            seekBars[3].setProgress(1800);
                            seekBars[4].setProgress(2000);
                            return;
                        case 10:
                            seekBars[0].setProgress(1500);
                            seekBars[1].setProgress(2300);
                            seekBars[2].setProgress(1900);
                            seekBars[3].setProgress(1600);
                            seekBars[4].setProgress(2500);
                            return;
                        case 11:
                            seekBars[0].setProgress(1330);
                            seekBars[1].setProgress(1770);
                            seekBars[2].setProgress(1550);
                            seekBars[3].setProgress(1280);
                            seekBars[4].setProgress(1700);
                            return;
                        case 12:
//                        seekBars[0].setProgress(1500);
//                        seekBars[1].setProgress(1500);
//                        seekBars[2].setProgress(1500);
//                        seekBars[3].setProgress(1500);
//                        seekBars[4].setProgress(1500);
                            return;
                        default:
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        switchCompat_bass_boost.setOnCheckedChangeListener((compoundButton, b) -> {
            playService.setBass(b);
            seekBar_bass.setEnabled(b);
            sp.save("Bass", b);
        });
        seekBar_bass.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                playService.setBassStrength((short) i);
                sp.save("Bass_seekBar", i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        switchCompat_virtualizer.setOnCheckedChangeListener((compoundButton, b) -> {
            playService.setVirtualizer(b);
            seekBar_virtualizer.setEnabled(b);
            sp.save("Virtualizer", b);
        });
        seekBar_virtualizer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                playService.setVirtualizerStrength((short) i);
                sp.save("Virtualizer_seekBar", i);
                Log.v("低音增强","值: "+ i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //次要
        switchCompat_acoustic_echo_canceler.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                if (AcousticEchoCanceler.isAvailable()) {
                    playService.setCanceler(b);
                    sp.save("Canceler", b);
                } else {
                    snackbar(R.string.snackbar_not_support_acoustic_echo_canceler);
                    switchCompat_acoustic_echo_canceler.setChecked(false);
                }
            } catch (Exception e) {
                Log.e("Phonograph", e.toString());
            }
        });
        switchCompat_automatic_gain_control.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                if (AutomaticGainControl.isAvailable()) {
                    playService.setControl(b);
                    sp.save("AutoGain", b);
                } else {
                    snackbar(R.string.snackbar_not_support_automatic_gain_control);
                    switchCompat_automatic_gain_control.setChecked(false);
                }
            } catch (Exception e) {
                Log.e("Phonograph", e.toString());
            }
        });
        switchCompat_noise_suppressor.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                if (NoiseSuppressor.isAvailable()) {
                    playService.setSuppressor(b);
                    sp.save("Suppressor", b);
                } else {
                    snackbar(R.string.snackbar_not_support_noise_suppressor);
                    switchCompat_noise_suppressor.setChecked(false);
                }
            } catch (Exception e) {
                Log.e("Phonograph", e.toString());
            }
        });
    }

    private void getPreference(){
        //均衡器
        if (!sp.getBoolean("Equalizer", false)) {
            seekBar_layout.setVisibility(View.GONE);
        } else {
            spinner.setSelection(sp.getInt("Spinner", 0));
            seekBar_layout.setVisibility(View.VISIBLE);
        }
        switchCompat_switch.setChecked(sp.getBoolean("Equalizer", false));
        spinner.setEnabled(sp.getBoolean("Equalizer", false));
        //低音增强
        switchCompat_bass_boost.setChecked(sp.getBoolean("Bass", false));
        seekBar_bass.setProgress(sp.getInt("Bass_seekBar", 0));
        //虚拟环绕
        switchCompat_virtualizer.setChecked(sp.getBoolean("Virtualizer", false));
        seekBar_virtualizer.setProgress(sp.getInt("Virtualizer_seekBar", 0));
        //次要
        switchCompat_acoustic_echo_canceler.setChecked(sp.getBoolean("Canceler", false));
        switchCompat_automatic_gain_control.setChecked(sp.getBoolean("AutoGain", false));
        switchCompat_noise_suppressor.setChecked(sp.getBoolean("Suppressor", false));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void snackbar(int msg) {
        Snackbar.make(layout, msg, Snackbar.LENGTH_LONG).show();
    }
}
