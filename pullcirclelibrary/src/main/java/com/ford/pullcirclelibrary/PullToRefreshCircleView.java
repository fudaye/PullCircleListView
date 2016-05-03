package com.ford.pullcirclelibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;


import java.lang.reflect.Field;

/**
 * @author Ford
 *
 */
public class PullToRefreshCircleView extends ListView {

    public static final String TAG = PullToRefreshCircleView.class.getSimpleName();

    private boolean mIsFooterReady = false;

    private float mDownY;

    private float mLastY;

    public float mMoveDeltaY = 0;

    private boolean isLayout = false;

    private boolean isPull = true;

    private float mDrag = 4;

    private boolean isRefreshing = false;

    private static final int HEAD_HEIGHT = 200;

    private boolean scrollFlag = false;

    private int lastVisibleItemPosition;

    private View mFootView;

    private View mHeadView;

    private boolean mEnablePullLoad = false;

    private ProgressBar mMeteor;

    private CircularProgressBar mCircularProgressBar;

    private OnRefreshListener mListener;

    public interface OnRefreshListener {
        void onRefresh();

        void onLoadMore();
    }

    OnScrollListener mDelegateOnScrollListener;

    public PullToRefreshCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullToRefreshCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        setOnScrollListener(mOnScrollListener);
        mHeadView = LayoutInflater.from(context).inflate(R.layout.activity_personal_home_head, this, false);
        mMeteor = (ProgressBar) mHeadView.findViewById(R.id.personal_home_head_meteor);
        mCircularProgressBar = (CircularProgressBar) mHeadView.findViewById(R.id.personal_home_head_circularprogressbar);
        addHeaderView(mHeadView);
        mFootView = LayoutInflater.from(context).inflate(R.layout.layout_common_bottom_loadmore, this, false);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        if (listener == mOnScrollListener) {
            super.setOnScrollListener(listener);
        } else {
            mDelegateOnScrollListener = listener;
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isLayout) {
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    AbsListView alv;
                    try {
                        alv = (AbsListView) v;
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                        return false;
                    }
                    int top = alv.getChildAt(0) == null ? 0 : alv.getChildAt(0).getTop();
                    isPull = alv.getCount() == 0 || alv.getFirstVisiblePosition() == 0 && top >= 0;
                    return false;
                }
            });
            setOnScrollListener(mOnScrollListener);
            isLayout = true;
        }
        super.onLayout(changed, l, t, r, b);

    }

    boolean isMove = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                mLastY = mDownY;
                if (ev.getY() < mMoveDeltaY)
                    return true;
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                if (isPull && !mMeteor.isShown()) {
                    mMoveDeltaY = mMoveDeltaY + (ev.getY() - mLastY) / mDrag;
                    if (mMoveDeltaY < 0)
                        mMoveDeltaY = 0;
                    if (mMoveDeltaY > getMeasuredHeight())
                        mMoveDeltaY = getMeasuredHeight();
                    if (!isRefreshing && mCircularProgressBar.getCurrentPro() < 180) {
                        mCircularProgressBar.setPro((int) mMoveDeltaY);
                    }
                    if (isPull) {
                        if (mHeadView != null) {
                            //  保留问题 当一个点下拉，另一个点点击屏幕造成回弹现象
                            if ((int) mMoveDeltaY > 180) {
                                mMoveDeltaY = 180;
                            }
                            mHeadView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtility.dip2px(getContext(), HEAD_HEIGHT) + (int) mMoveDeltaY));
                            invalidate();
                        }
                    }
                }
                mLastY = ev.getY();
                requestLayout();
                if (mMoveDeltaY > 8) {
                    clearContentViewEvents();
                }
                if (mMoveDeltaY > 0) {
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                // 慢慢收回
                if (isPull) {
                    mHeadView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtility.dip2px(getContext(), HEAD_HEIGHT)));
                }
                mMoveDeltaY = 0;
                if (mCircularProgressBar.getCurrentPro() < 180.0 && !isRefreshing) {
                    noRefresh();
                }
                if (mCircularProgressBar.getCurrentPro() >= 180 && !isRefreshing) {
                    mCircularProgressBar.setCurrentPro(0);
                    isRefreshing = true;
                    if (mListener != null) {
                        mListener.onRefresh();
                        mMeteor.setVisibility(VISIBLE);
                    }
                    stopSpin();
                }
        }
        // 防止下拉刷新中误出发点击事件 这样无法执行上啦操作
        if (isMove && mLastY > mDownY && isPull) {
            isMove = false;
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void noRefresh() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (mCircularProgressBar.getCurrentPro() > 0) {
                    handler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mCircularProgressBar.decreasingProgress();
        }
    };

    public void stopSpin() {
        isRefreshing = false;
        mCircularProgressBar.stopSpinning();
    }

    public void startSpin() {
        mCircularProgressBar.spin();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * 通过反射修改字段去掉长按事件和点击事件
     */
    private void clearContentViewEvents() {
        try {
            Field[] fields = AbsListView.class.getDeclaredFields();
            for (Field field : fields)
                if (field.getName().equals("mPendingCheckForLongPress")) {
                    field.setAccessible(true);
                    getHandler().removeCallbacks((Runnable) field.get(this));
                } else if (field.getName().equals("mTouchMode")) {
                    field.setAccessible(true);
                    field.set(this, -1);
                }
            (this).getSelector().setState(new int[]{0});
        } catch (Exception e) {
            Log.d(TAG, "error : " + e.toString());
        }
    }

    /**
     * 执行下拉刷新
     */
    private void executeOnLastItemVisible() {
        if (!mFootView.isShown()) {
            mListener.onLoadMore();
            showFooterView();
        }
    }

    public void showFooterView() {
        if (mFootView != null) {
            mFootView.setVisibility(View.VISIBLE);
        }
    }


    public void hideLastItemView() {
        if (mFootView != null) {
            mFootView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!mIsFooterReady) {
            mIsFooterReady = true;
            addFooterView(mFootView);
        }
        super.setAdapter(adapter);
    }


    public View getHeadView() {
        return mHeadView;
    }

    public View getFootView() {
        return mFootView;
    }

    private final OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mDelegateOnScrollListener != null) {
                mDelegateOnScrollListener.onScrollStateChanged(view, scrollState);
            }
            // TODO 增加上啦加载更多
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    scrollFlag = true;
                    break;
                case OnScrollListener.SCROLL_STATE_FLING:
                    scrollFlag = true;
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mDelegateOnScrollListener != null) {
                mDelegateOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            ListAdapter adapter = getAdapter();
            if (adapter == null || visibleItemCount == 0)
                return;
            int itemCount = firstVisibleItem + visibleItemCount;
            if (scrollFlag) {
                if (firstVisibleItem > lastVisibleItemPosition) {
                    if (itemCount == totalItemCount) {
                        if (mListener != null && mFootView != null && mEnablePullLoad) {
                            executeOnLastItemVisible();
                        }
                    } else {
                        hideLastItemView();
                    }
                }
                if (firstVisibleItem == lastVisibleItemPosition) {
                    return;
                }
                lastVisibleItemPosition = firstVisibleItem;
            }
        }
    };


    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            removeFooterView(mFootView);
        } else {
            addFooterView(mFootView);
            mFootView.setVisibility(VISIBLE);
        }
    }

    public void stopRefresh() {
        mMeteor.setVisibility(GONE);
    }

    public void stopLoadMore() {
        mFootView.setVisibility(GONE);
    }
}
