
package com.thb.pullrefresh.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thb.pullrefresh.R;

public class RFooter extends LinearLayout {

    public final static int STATE_NORMAL = 1;
    public final static int STATE_READY = 2;
    public final static int STATE_LOADING = 3;

    private LinearLayout mContent;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private int mLastState;

    public RFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public RFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RFooter(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContent = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.rlistview_footer,
                null);
        this.addView(mContent);
        mContent.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        mProgressBar = (ProgressBar) findViewById(R.id.footer_progressbar);
        mTextView = (TextView) findViewById(R.id.footer_txt);
    }

    public void setState(int state) {
        if (mLastState == state)
            return;
        switch (state) {
            case STATE_NORMAL:
                mTextView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mTextView.setText(getResources().getString(R.string.refresh_load_more));
                break;
            case STATE_READY:
                mTextView.setText(getResources().getString(R.string.refresh_pull_up));
                break;
            case STATE_LOADING:
                mTextView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
            default:
                break;
        }
        mLastState = state;
    }

    public void setBottomMargin(int margin) {
        if (0 >= margin)
            return;
        LayoutParams params = (LayoutParams) mContent.getLayoutParams();
        params.bottomMargin = margin;
        mContent.setLayoutParams(params);
    }

    public int getBootomMargin() {
        LayoutParams params = (LayoutParams) mContent.getLayoutParams();
        return params.bottomMargin;
    }

}
