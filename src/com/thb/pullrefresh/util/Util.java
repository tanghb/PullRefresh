
package com.thb.pullrefresh.util;

import android.content.Context;

import com.thb.pullrefresh.R;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Util {

    public static String getLastUpdateTime(Context context, long lastUpdateTime) {
        long now = System.currentTimeMillis();
        // 上次更新时间与现在时间相隔分钟数
        long dis = (now - lastUpdateTime) / (1000 * 60);
        String result = "";
        // 小于一分钟，显示刚刚更新
        if (dis < 1) {
            result = context.getString(R.string.refresh_just_now);
        } else {
            if (dis < 60) {
                result = String.format(context.getString(R.string.refresh_minute_before), dis);
            } else {
                // 小时
                dis = dis / 60;
                if (dis < 24) {
                    result = String.format(context.getString(R.string.refresh_hour_before), dis);
                } else {
                    // 不是今天更新的，直接显示时间
                    Date lastUpdate = new Date(lastUpdateTime);
                    String format = context.getString(R.string.refresh_date_format);
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    result = sdf.format(lastUpdate);
                }
            }
        }
        return result;
    }

}
