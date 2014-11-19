package com.thb.pullrefresh.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thb.pullrefresh.R;


/**
 * @author tanghb
 */
public class RHeader extends LinearLayout {

    private static final String TAG = "RHeader";
    private static final boolean DEBUG = true;

    private LinearLayout mContainer;

    /** 正在刷新 */
    public static final int STATE_REFRESHING = 1;
    /** 下拉状态 */
    public static final int STATE_PULLING = 2;
    /** 松开可以刷新 */
    public static final int STATE_READY = 3;
    /** 当前状态 */
    private int mLastState;

    private Animation mAnimationUp;
    private Animation mAnimationDown;

    private TextView mPull;
    private TextView mUpdateTime;
    private ImageView mArrow;
    private ProgressBar mProgressBar;

    public RHeader(Context context) {
        super(context);
        init(context);
    }

    public RHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.rlistview_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);
        mPull = (TextView) findViewById(R.id.txt_pull);
        mUpdateTime = (TextView) findViewById(R.id.txt_update_time);
        mArrow = (ImageView) findViewById(R.id.img_arrow);
        mProgressBar = (ProgressBar) findViewById(R.id.header_progressbar);

        mAnimationUp = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationUp.setDuration(180);
        mAnimationUp.setFillAfter(true);
        mAnimationDown = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationDown.setDuration(180);
        mAnimationDown.setFillAfter(true);
    }

    public void setState(int state) {
        if (mLastState == state)
            return;
        if (state == STATE_REFRESHING) {
            mArrow.clearAnimation();
            mArrow.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mArrow.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        switch (state) {
            case STATE_PULLING:
                mPull.setText(getResources().getString(R.string.refresh_pull_down));
                if (mLastState == STATE_READY) {
                    // 如果上一次传入的状态是 松开可以刷新，这次传入的状态变成了下拉状态，需要将图片旋转
                    mArrow.startAnimation(mAnimationDown);
                }
                if (mLastState == STATE_REFRESHING) {
                    mArrow.clearAnimation();
                }
                break;
            case STATE_READY:
                mPull.setText(getResources().getString(R.string.refresh_pull_up));
                mArrow.startAnimation(mAnimationUp);
                break;
            case STATE_REFRESHING:
                mPull.setText(getResources().getString(R.string.refresh_pull_refreshing));
                break;
            default:
                break;
        }
        mLastState = state;
    }

    /**
     * 设置上次更新时间
     * 
     * @param time
     */
    public void setUpdateTime(String time) {
        String text = getResources().getString(R.string.refresh_update_time);
        text = String.format(text, time);
        mUpdateTime.setText(text);
    }

    /**
     * 设置下拉头可见高度
     */
    public void setVisibleHeight(int height) {
        if (height < 0)
            height = 0;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContainer
                .getLayoutParams();
        params.height = height;
        mContainer.setLayoutParams(params);
    }

    /**
     * 获取下拉刷新头的可见高度
     * 
     * @return
     */
    public int getVisibleHeight() {
        return mContainer.getHeight();
    }

}
