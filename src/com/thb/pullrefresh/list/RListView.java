
package com.thb.pullrefresh.list;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.thb.pullrefresh.R;

/**
 * @author tanghb
 */
public class RListView extends ListView implements OnScrollListener {

    private static final String TAG = "RListView";
    private static final boolean DEBUG = true;

    private final static int PULL_LOAD_MORE_DATA = 50;

    private Scroller mScroller;
    private OnScrollListener mScrollListener;
    private IRListViewListener mRListener;

    /** 上次更新时间是否设置过,若设置过，则不再设置（每次下拉时才更新一次上次更新时间，下拉过程中不在更新） */
    private boolean mIsSetUpdateTime;

    private RHeader mHeader;
    /** 是否可以下拉 */
    private boolean mPullDownAble = true;
    /** 是否正在刷新 */
    private boolean mRefreshing = false;

    private RFooter mFooter;
    /** 是否可以上拉加载 */
    private boolean mPullLoadAble = true;
    /** 是否正在加载 */
    private boolean mLoading = false;
    /** 是否已经添加了footer */
    private boolean mIsAddFooter = false;

    /** item 总的数量 */
    private int mTotalItemCount;
    /** 重置header高度时间 */
    private static final int SCROLL_DURATION = 400;
    private static final float OFFSET_RADIO = 1.8f;
    /** 重置下拉刷新头高度 */
    private static final int RESET_HEADER = 0;
    /** 重置上拉加载头高度 */
    private static final int RESET_FOOTER = 1;
    /** 当前正在重置的控件 */
    private int mCurrentSet;

    private float mLastY;

    private RelativeLayout mHeaderView;
    private int mHeaderHeight;

    public RListView(Context context) {
        super(context);
        init(context);
    }

    public RListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);
        mHeader = new RHeader(context);
        mHeaderView = (RelativeLayout) mHeader.findViewById(R.id.header_relative);
        mFooter = new RFooter(context);
        addHeaderView(mHeader);
        mHeader.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        mHeaderHeight = mHeaderView.getHeight();
                        if (DEBUG)
                            Log.i(TAG, "mHeaderHeight is --- " + mHeaderHeight);
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
        mFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadMore();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float yDistance = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (null != mRListener) {
                    if (getFirstVisiblePosition() == 0
                            && (mHeader.getVisibleHeight() > 0 || yDistance > 0)) {
                        updateHeaderHeight(yDistance / OFFSET_RADIO);
                    } else if (getLastVisiblePosition() == mTotalItemCount - 1
                            && (mFooter.getBootomMargin() > 0 || yDistance < 0)) {
                        updateFooterHeight(-yDistance / OFFSET_RADIO);
                    }
                }
                break;
            default:
                if (getFirstVisiblePosition() == 0) {
                    if (mPullDownAble) {
                        if (mHeader.getVisibleHeight() > mHeaderHeight) {
                            mHeader.setState(RHeader.STATE_REFRESHING);
                            if (null != mRListener && !mRefreshing) {
                                mRListener.onRefresh();
                                mRefreshing = true;
                                mIsSetUpdateTime = false;
                            }
                        }
                        resetHeader();
                    }
                }
                if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    if (mPullLoadAble && mFooter.getBootomMargin() > PULL_LOAD_MORE_DATA) {
                        startLoadMore();
                        resetFooterHeight();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 停止刷新
     */
    public void stopRefreshing() {
        if (mRefreshing) {
            mRefreshing = false;
            resetHeader();
        }
    }

    public void stopLoadMore() {
        if (mLoading) {
            mLoading = false;
            mFooter.setState(RFooter.STATE_NORMAL);
            resetFooterHeight();
        }
    }

    private void updateHeaderHeight(float du) {
        if (mPullDownAble) {
            setUpdateTime();
            mHeader.setVisibleHeight((int) du + mHeader.getVisibleHeight());
            if (!mRefreshing) {
                if (mHeader.getVisibleHeight() > mHeaderHeight)
                    mHeader.setState(RHeader.STATE_READY);
                else
                    mHeader.setState(RHeader.STATE_PULLING);
            }
            // 不加这句话，会出现问题，就是下拉后不想刷新手指往上移动，但显示会有问题，具体问题可以试一试
            setSelection(0);
        }
    }

    /**
     * 上次更新时间
     */
    private void setUpdateTime() {
        if (mIsSetUpdateTime)
            return;
        mIsSetUpdateTime = true;

        if (null != mRListener) {
            mRListener.onUpdateTime(mHeader);
        }
    }

    /**
     * 1、隐藏header（已经刷新完成或者未达到刷新高度就松开手指）
     * 2、如果下拉高度大于header实际高度，则松开手指后header高度变为实际高度
     */
    private void resetHeader() {
        int visibleHeight = mHeader.getVisibleHeight();
        if (0 >= visibleHeight)
            return;
        if (mRefreshing && visibleHeight <= mHeaderHeight)
            return;
        int finalHeight = 0;
        if (mRefreshing && visibleHeight > mHeaderHeight)
            finalHeight = mHeaderHeight;
        int dy = finalHeight - visibleHeight;
        mCurrentSet = RESET_HEADER;
        mScroller.startScroll(0, visibleHeight, 0, dy, SCROLL_DURATION);
        invalidate();
    }

    private void updateFooterHeight(float distance) {
        int height = mFooter.getBootomMargin() + (int) distance;
        if (height > PULL_LOAD_MORE_DATA) {
            mFooter.setState(RFooter.STATE_READY);
        } else {
            mFooter.setState(RFooter.STATE_NORMAL);
        }
        mFooter.setBottomMargin(height);
    }

    private void resetFooterHeight() {
        int bottom = mFooter.getBootomMargin();
        if (bottom > 0) {
            mCurrentSet = RESET_FOOTER;
            mScroller.startScroll(0, bottom, 0, -bottom, SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        if (!mLoading) {
            mFooter.setState(RFooter.STATE_LOADING);
            if (null != mRListener && !mLoading) {
                mRListener.onLoad();
                mLoading = true;
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mCurrentSet == RESET_HEADER) {
                mHeader.setVisibleHeight(mScroller.getCurrY());
            } else if (mCurrentSet == RESET_FOOTER) {
                mFooter.setBottomMargin(mScroller.getCurrY());
            }
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (null != mScrollListener)
            mScrollListener.onScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        mTotalItemCount = totalItemCount;
        if (null != mScrollListener)
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mPullLoadAble && !mIsAddFooter) {
            addFooterView(mFooter);
            mIsAddFooter = true;
        }
        super.setAdapter(adapter);
    }

    /**
     * 设置是否可以下拉刷新
     * 
     * @param able
     */
    public void setPullDownAble(boolean able) {
        mPullDownAble = able;
    }

    /**
     * 设置是否可以上拉加载
     * 
     * @param able
     */
    public void setPullLoadAble(boolean able) {
        mPullLoadAble = able;
        if (!able) {
            mFooter.setVisibility(View.GONE);
            mFooter.setClickable(false);
        }
    }

    public void setRListViewListener(IRListViewListener l) {
        mRListener = l;
    }

    public interface IRListViewListener {
        public void onRefresh();

        public void onLoad();

        /**
         * 更新上次更新列表时间
         */
        public void onUpdateTime(RHeader header);
    }
}
