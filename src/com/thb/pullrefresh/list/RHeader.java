/**
 * 
 */

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

    /** ����ˢ�� */
    public static final int STATE_REFRESHING = 1;
    /** ����״̬ */
    public static final int STATE_PULLING = 2;
    /** �ɿ�����ˢ�� */
    public static final int STATE_READY = 3;
    /** ��ǰ״̬ */
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
            mArrow.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mArrow.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
        switch (state) {
            case STATE_PULLING:
                mPull.setText(getResources().getString(R.string.refresh_pull_down));
                if (mLastState == STATE_READY) {
                    // �����һ�δ����״̬�� �ɿ�����ˢ�£���δ����״̬����� ����״̬����Ҫ��ͼƬ��ת
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
     * �����ϴθ���ʱ��
     * 
     * @param time
     */
    public void setUpdateTime(String time) {
        String text = getResources().getString(R.string.refresh_update_time);
        text = String.format(text, time);
        mUpdateTime.setText(text);
    }

    /**
     * ��������ͷ�ɼ��߶�
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
     * ��ȡ����ˢ��ͷ�Ŀɼ��߶�
     * 
     * @return
     */
    public int getVisibleHeight() {
        return mContainer.getHeight();
    }

}
