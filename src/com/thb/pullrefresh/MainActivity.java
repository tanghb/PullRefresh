
package com.thb.pullrefresh;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;

import com.thb.pullrefresh.config.Config;
import com.thb.pullrefresh.list.RHeader;
import com.thb.pullrefresh.list.RListView;
import com.thb.pullrefresh.list.RListView.IRListViewListener;
import com.thb.pullrefresh.util.Util;

import java.util.ArrayList;

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
        refreshItems();
        mListView = (RListView) findViewById(R.id.xListView);
        mAdapter = new ArrayAdapter<String>(this, R.layout.item, items);
        mListView.setAdapter(mAdapter);
        mListView.setRListViewListener(this);
    }

    private void refreshItems() {
        for (int i = 0; i != 15; ++i) {
            items.add("refresh cnt " + (++start));
        }
        setUpdateTime();
    }

    private void loadItems() {
        for (int i = 0; i != 15; ++i) {
            items.add("refresh cnt " + (++start));
        }
        setUpdateTime();
    }

    private void setUpdateTime() {
        SharedPreferences sp = getSharedPreferences(Config.UPDATE_INFOS_KEY,
                Context.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        sp.edit().putLong(Config.LAST_UPDATE_TIME_KEY, now).commit();
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshItems();
                mAdapter.notifyDataSetChanged();
                mListView.stopRefreshing();
            }
        }, 3000);
    }

    @Override
    public void onLoad() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadItems();
                mAdapter.notifyDataSetChanged();
                mListView.stopLoadMore();
            }
        }, 3000);
    }

    @Override
    public void onUpdateTime(RHeader header) {
        SharedPreferences sp = getSharedPreferences(Config.UPDATE_INFOS_KEY,
                Context.MODE_PRIVATE);
        long time = sp.getLong(Config.LAST_UPDATE_TIME_KEY, 0);
        String updateTime = Util.getLastUpdateTime(this, time);
        header.setUpdateTime(updateTime);
    }
}
