package com.peter1303.phonograph.ui.activities.intro;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.peter1303.phonograph.R;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AppIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonCtaVisible(true);
        setButtonNextVisible(false);
        setButtonBackVisible(false);

        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_TEXT);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.welcome_to_phonograph)
                .image(R.drawable.icon_web)
                .background(R.color.md_blue_grey_100)
                .backgroundDark(R.color.md_blue_grey_200)
                .layout(R.layout.fragment_simple_slide_large_image)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.label_playing_queue)
                .description(R.string.open_playing_queue_instruction)
                .image(R.drawable.tutorial_queue_swipe_up)
                .background(R.color.md_teal_500)
                .backgroundDark(R.color.md_teal_600)
                .layout(R.layout.fragment_simple_slide_large_image)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.label_playing_queue)
                .description(R.string.rearrange_playing_queue_instruction)
                .image(R.drawable.tutorial_rearrange_queue)
                .background(R.color.md_green_500)
                .backgroundDark(R.color.md_green_600)
                .layout(R.layout.fragment_simple_slide_large_image)
                .build());
    }
}
