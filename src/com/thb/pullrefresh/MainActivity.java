
package com.thb.pullrefresh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;

import com.thb.pullrefresh.config.Config;
import com.thb.pullrefresh.list.RListView;
import com.thb.pullrefresh.list.RListView.IRListViewListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity implements IRListViewListener {

    private RListView mListView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> items = new ArrayList<String>();
    private int start = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geneItems();
        mListView = (RListView) findViewById(R.id.xListView);
        mAdapter = new ArrayAdapter<String>(this, R.layout.item, items);
        mListView.setAdapter(mAdapter);
        mListView.setRListViewListener(this);
    }

    private void geneItems() {
        for (int i = 0; i != 5; ++i) {
            items.add("refresh cnt " + (++start));
        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                geneItems();
                mAdapter.notifyDataSetChanged();
                mListView.stopRefreshing();
                SharedPreferences sp = getSharedPreferences(Config.UPDATE_INFOS_KEY,
                        Context.MODE_PRIVATE);
                long now = System.currentTimeMillis();
                Date lastUpdate = new Date(now);
                String format = getString(R.string.refresh_date_format);
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sp.edit().putLong(Config.LAST_UPDATE_TIME_KEY, now).commit();
            }
        }, 3000);
    }

    @Override
    public void onLoad() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.stopLoadMore();
            }
        }, 3000);
    }
}
