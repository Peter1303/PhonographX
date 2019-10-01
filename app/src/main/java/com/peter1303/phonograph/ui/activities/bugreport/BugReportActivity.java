/*
 * Peter1303
 * Copyright (c) 2019.
 */

package com.peter1303.phonograph.ui.activities.bugreport;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.annotation.StringDef;
import androidx.annotation.StringRes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.TintHelper;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.ui.activities.base.AbsThemeActivity;
import com.peter1303.phonograph.ui.activities.bugreport.model.DeviceInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BugReportActivity extends AbsThemeActivity {

    private static final String RESULT_OK = "RESULT_OK";
    private static final String RESULT_UNKNOWN = "RESULT_UNKNOWN";

    private DeviceInfo deviceInfo;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_layout_title)
    TextInputLayout inputLayoutTitle;
    @BindView(R.id.input_title)
    TextInputEditText inputTitle;
    @BindView(R.id.input_layout_description)
    TextInputLayout inputLayoutDescription;
    @BindView(R.id.input_description)
    TextInputEditText inputDescription;
    @BindView(R.id.air_textDeviceInfo)
    TextView textDeviceInfo;

    @BindView(R.id.button_send)
    FloatingActionButton sendFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);
        ButterKnife.bind(this);

        setStatusBarColorAuto();
        setNavigationBarColorAuto();
        setTaskDescriptionColorAuto();

        initViews();

        setTitle(R.string.report_an_issue);

        deviceInfo = new DeviceInfo(this);
        textDeviceInfo.setText(deviceInfo.toString());
    }

    private void initViews() {
        final int accentColor = ThemeStore.accentColor(this);
        final int primaryColor = ThemeStore.primaryColor(this);
        toolbar.setBackgroundColor(primaryColor);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textDeviceInfo.setOnClickListener(v -> copyDeviceInfoToClipBoard());

        TintHelper.setTintAuto(sendFab, accentColor, true);
        sendFab.setOnClickListener(v -> reportIssue());

        TintHelper.setTintAuto(inputTitle, accentColor, false);
        TintHelper.setTintAuto(inputDescription, accentColor, false);
    }

    private void reportIssue() {
    }

    private void copyDeviceInfoToClipBoard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.device_info), deviceInfo.toMarkdown());
        clipboard.setPrimaryClip(clip);
        // TODO SnackBar
        Toast.makeText(BugReportActivity.this, R.string.copied_device_info_to_clipboard, Toast.LENGTH_LONG).show();
    }

    private boolean validateInput() {
        boolean hasErrors = false;

        if (TextUtils.isEmpty(inputTitle.getText())) {
            setError(inputLayoutTitle, R.string.bug_report_no_title);
            hasErrors = true;
        } else {
            removeError(inputLayoutTitle);
        }

        if (TextUtils.isEmpty(inputDescription.getText())) {
            setError(inputLayoutDescription, R.string.bug_report_no_description);
            hasErrors = true;
        } else {
            removeError(inputLayoutDescription);
        }

        return !hasErrors;
    }

    private void setError(TextInputLayout editTextLayout, @StringRes int errorRes) {
        editTextLayout.setError(getString(errorRes));
    }

    private void removeError(TextInputLayout editTextLayout) {
        editTextLayout.setError(null);
    }

    private void sendBugReport() {
        if (!validateInput()) return;

        String bugTitle = inputTitle.getText().toString();
        String bugDescription = inputDescription.getText().toString();

        //ReportIssueAsyncTask.report(this, report, target, login);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    /*
    private static class ReportIssueAsyncTask extends DialogAsyncTask<Void, Void, String> {
        private final Report report;

        public static void report(Activity activity, Report report, GithubTarget target,
                                  GithubLogin login) {
            new ReportIssueAsyncTask(activity, report, target, login).execute();
        }

        private ReportIssueAsyncTask(Activity activity, Report report, GithubTarget target,
                                     GithubLogin login) {
            super(activity);
            this.report = report;
            this.target = target;
            this.login = login;
        }

        @Override
        protected Dialog createDialog(@NonNull Context context) {
            return new MaterialDialog.Builder(context)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .title(R.string.bug_report_uploading)
                    .show();
        }

        @Override
        @Result
        protected String doInBackground(Void... params) {
            return RESULT_UNKNOWN;
        }

        @Override
        protected void onPostExecute(@Result String result) {
            super.onPostExecute(result);
            Context context = getContext();
            if (context == null) return;

            switch (result) {
                case RESULT_OK:
                    tryToFinishActivity();
                    break;
                case RESULT_BAD_CREDENTIALS:
                    new MaterialDialog.Builder(context)
                            .title(R.string.bug_report_failed)
                            .content(R.string.bug_report_failed_wrong_credentials)
                            .positiveText(android.R.string.ok)
                            .show();
                    break;
                case RESULT_INVALID_TOKEN:
                    new MaterialDialog.Builder(context)
                            .title(R.string.bug_report_failed)
                            .content(R.string.bug_report_failed_invalid_token)
                            .positiveText(android.R.string.ok)
                            .show();
                    break;
                case RESULT_ISSUES_NOT_ENABLED:
                    new MaterialDialog.Builder(context)
                            .title(R.string.bug_report_failed)
                            .content(R.string.bug_report_failed_issues_not_available)
                            .positiveText(android.R.string.ok)
                            .show();
                    break;
                default:
                    new MaterialDialog.Builder(context)
                            .title(R.string.bug_report_failed)
                            .content(R.string.bug_report_failed_unknown)
                            .positiveText(android.R.string.ok)
                            .onPositive((dialog, which) -> tryToFinishActivity())
                            .cancelListener(dialog -> tryToFinishActivity())
                            .show();
                    break;
            }
        }

        private void tryToFinishActivity() {
            Context context = getContext();
            if (context instanceof Activity && !((Activity) context).isFinishing()) {
                ((Activity) context).finish();
            }
        }
    }
     */
}
