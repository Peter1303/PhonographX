package com.peter1303.phonograph.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.kabouzeid.appthemehelper.color.MaterialColor;
import com.peter1303.phonograph.App;
import com.peter1303.phonograph.R;
import com.peter1303.phonograph.model.Purchase;
import com.peter1303.phonograph.ui.activities.base.AbsBaseActivity;
import com.peter1303.phonograph.util.ApiUtils;
import com.peter1303.phonograph.util.PurchaseUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchaseActivity extends AbsBaseActivity {

    private Context context = this;

    private static final int ACTIVITY_COLOR = MaterialColor.Green._500.getAsColor();

    private static final String PAY_URL = "https://pdev.top/pay/?select=5&id=";
    private static final String PAY_API = "https://pdev.top/phonograph/api/";

    @BindView(R.id.activity_purchase_layout)
    RelativeLayout layout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.restore_button)
    Button restoreButton;
    @BindView(R.id.purchase_button)
    Button purchaseButton;

    private AsyncTask restorePurchaseAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        setDrawUnderStatusBar();
        ButterKnife.bind(this);

        setStatusBarColor(ACTIVITY_COLOR);
        setNavigationBarColor(ACTIVITY_COLOR);
        setTaskDescriptionColor(ACTIVITY_COLOR);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.buy_pro));

        restoreButton.setOnClickListener(v -> {
            if (restorePurchaseAsyncTask == null || restorePurchaseAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
                restorePurchase();
            }
        });
        purchaseButton.setOnClickListener(v -> openUrl(PAY_URL + App.ANDROID_ID));
    }

    private void restorePurchase() {
        if (restorePurchaseAsyncTask != null) {
            restorePurchaseAsyncTask.cancel(false);
        }
        restorePurchaseAsyncTask = new RestorePurchaseAsyncTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private class RestorePurchaseAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Map<String, String> list = new HashMap<>();
            list.put(ApiUtils.MODE, ApiUtils.PURCHASE);
            list.put(ApiUtils.ID, App.ANDROID_ID);
            String result = post(PAY_API, list);
            Gson gson = new Gson();
            Purchase purchase = gson.fromJson(result, Purchase.class);
            new PurchaseUtil(context).setProVersion(purchase.isPurchased());
            return purchase.isPurchased();
            //cancel(false);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            Snackbar.make(layout, b ?
                    R.string.restored_previous_purchase_please_restart :
                    R.string.could_not_restore_purchase, Snackbar.LENGTH_LONG).show();
            super.onPostExecute(b);
        }
    }
}
