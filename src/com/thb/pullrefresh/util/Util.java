
package com.thb.pullrefresh.util;

import android.content.Context;

import com.thb.pullrefresh.R;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Util {

    public static String getLastUpdateTime(Context context, long lastUpdateTime) {
        long now = System.currentTimeMillis();
        // �ϴθ���ʱ��������ʱ�����������
        long dis = (now - lastUpdateTime) / (1000 * 60);
        String result = "";
        // С��һ���ӣ���ʾ�ոո���
        if (dis < 1) {
            result = context.getString(R.string.refresh_just_now);
        } else {
            if (dis < 60) {
                result = String.format(context.getString(R.string.refresh_minute_before), dis);
            } else {
                // Сʱ
                dis = dis / 60;
                if (dis < 24) {
                    result = String.format(context.getString(R.string.refresh_hour_before), dis);
                } else {
                    // ���ǽ�����µģ�ֱ����ʾʱ��
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
