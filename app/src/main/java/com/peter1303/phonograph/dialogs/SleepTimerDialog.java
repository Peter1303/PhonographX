package com.peter1303.phonograph.dialogs;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.DialogFragment;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.peter1303.phonograph.App;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.helper.MusicPlayerRemote;
import com.peter1303.phonograph.service.MusicService;
import com.peter1303.phonograph.ui.activities.PurchaseActivity;
import com.peter1303.phonograph.util.MusicUtil;
import com.peter1303.phonograph.util.PreferenceUtil;
import com.peter1303.phonograph.util.PurchaseUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class SleepTimerDialog extends DialogFragment {
    // TODO 更好看的样式
    @BindView(R.id.seek_bar)
    AppCompatSeekBar seekBar;
    @BindView(R.id.should_finish_last_song)
    CheckBox shouldFinishLastSong;

    private int seekBarProgress;
    private MaterialDialog materialDialog;
    private TimerUpdater timerUpdater;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        timerUpdater.cancel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        timerUpdater = new TimerUpdater();
        materialDialog = new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .title(getActivity().getResources().getString(R.string.action_sleep_timer))
                .positiveText(R.string.action_set)
                .onPositive((dialog, which) -> {
                    if (getActivity() == null) {
                        return;
                    }

                    PreferenceUtil.getInstance(getActivity()).setSleepTimerFinishMusic(shouldFinishLastSong.isChecked());

                    final int minutes = seekBarProgress;

                    PendingIntent pi = makeTimerPendingIntent(PendingIntent.FLAG_CANCEL_CURRENT);

                    final long nextSleepTimerElapsedTime = SystemClock.elapsedRealtime() + minutes * 60 * 1000;
                    PreferenceUtil.getInstance(getActivity()).setNextSleepTimerElapsedRealtime(nextSleepTimerElapsedTime);
                    AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleepTimerElapsedTime, pi);

                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.sleep_timer_set, minutes), Toast.LENGTH_SHORT).show();
                })
                .onNeutral((dialog, which) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    final PendingIntent previous = makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE);
                    if (previous != null) {
                        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        am.cancel(previous);
                        previous.cancel();
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.sleep_timer_canceled), Toast.LENGTH_SHORT).show();
                    }

                    MusicService musicService = MusicPlayerRemote.musicService;
                    if (musicService != null && musicService.pendingQuit) {
                        musicService.pendingQuit = false;
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.sleep_timer_canceled), Toast.LENGTH_SHORT).show();
                    }
                })
                .showListener(dialog -> {
                    if (makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE) != null) {
                        timerUpdater.start();
                    }
                })
                .customView(R.layout.dialog_sleep_timer, false)
                .build();

        if (getActivity() == null || materialDialog.getCustomView() == null) {
            return materialDialog;
        }

        ButterKnife.bind(this, materialDialog.getCustomView());

        boolean finishMusic = PreferenceUtil.getInstance(getActivity()).getSleepTimerFinishMusic();
        shouldFinishLastSong.setChecked(finishMusic);
        seekBar.setMax(120);
        //seekBar.setProgressColor(ThemeSingleton.get().positiveColor.getDefaultColor());
        seekBar.getThumb().setColorFilter(ThemeSingleton.get().positiveColor.getDefaultColor() , PorterDuff.Mode.SRC_ATOP);
        seekBarProgress = PreferenceUtil.getInstance(getActivity()).getLastSleepTimerValue();
        updateTimeDisplayTime();
        seekBar.setProgress(seekBarProgress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < 1) {
                    seekBar.setProgress(1);
                    return;
                }
                seekBarProgress = i;
                updateTimeDisplayTime();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PreferenceUtil.getInstance(Objects.requireNonNull(getActivity())).setLastSleepTimerValue(seekBarProgress);
            }
        });
        return materialDialog;
    }

    @SuppressLint("SetTextI18n")
    private void updateTimeDisplayTime() {
        materialDialog.setTitle(seekBarProgress + getContext().getResources().getString(R.string.dialog_sleeper_min));
    }

    private PendingIntent makeTimerPendingIntent(int flag) {
        return PendingIntent.getService(getActivity(), 0, makeTimerIntent(), flag);
    }

    private Intent makeTimerIntent() {
        Intent intent = new Intent(getActivity(), MusicService.class);
        if (shouldFinishLastSong.isChecked()) {
            return intent.setAction(MusicService.ACTION_PENDING_QUIT);
        }
        return intent.setAction(MusicService.ACTION_QUIT);
    }

    private void updateCancelButton() {
        MusicService musicService = MusicPlayerRemote.musicService;
        if (musicService != null && musicService.pendingQuit) {
            materialDialog.setActionButton(DialogAction.NEUTRAL, materialDialog.getContext().getString(R.string.cancel_current_timer));
        } else {
            materialDialog.setActionButton(DialogAction.NEUTRAL, null);
        }
    }

    private class TimerUpdater extends CountDownTimer {
        TimerUpdater() {
            super(PreferenceUtil.getInstance(getActivity()).getNextSleepTimerElapsedRealTime() - SystemClock.elapsedRealtime(), 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            materialDialog.setActionButton(DialogAction.NEUTRAL, materialDialog.getContext().getString(R.string.cancel_current_timer) + " (" + MusicUtil.getReadableDurationString(millisUntilFinished) + ")");
        }

        @Override
        public void onFinish() {
            updateCancelButton();
        }
    }
}
