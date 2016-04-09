package com.jay.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created on 2016/4/9
 *
 * @author Q.Jay
 * @version 1.0.0
 */
public class SimpleRecyclerView extends RecyclerView {
    private View emptyView;
    private OnLoadingListener mOnLoadingListener;
    private boolean isLoading;

    public SimpleRecyclerView(Context context) {
        this(context, null);
    }

    public SimpleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    void init(Context context) {
        setHasFixedSize(true);
    }



    public void setEmptyView(View view) {
        if (view == null) {
            throw new NullPointerException("view is null");
        }
        emptyView = view;
        emptyView.setVisibility(GONE);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mAdapterDataObserver);
    }

    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            Adapter adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                updateEmptyStatus(getAdapter().getItemCount() == 0);
            }
        }
    };

    /**
     * 更新空状态
     *
     * @param empty 是否为空 {@link Boolean}
     */
    private void updateEmptyStatus(boolean empty) {
        if (empty) {
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                // If the caller just removed our empty view, make sure the list view is visible
                setVisibility(View.VISIBLE);
            }
        } else {
            if (emptyView != null) emptyView.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置加载监听器
     *
     * @param listener {@link OnLoadingListener}
     */
    public void setOnLoadingListener(OnLoadingListener listener) {
        this.mOnLoadingListener = listener;
        this.addOnScrollListener(mOnScrollListener);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (null != mOnLoadingListener && dy > 0 && !isLoading()) {
                int lastVisiblePosition;
                boolean isLoad = false;
                LayoutManager layoutManager = getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition();
                    if (lastVisiblePosition + 1 == recyclerView.getAdapter().getItemCount()) {
                        isLoad = true;
                    }
                } else if (layoutManager instanceof LinearLayoutManager) {
                    lastVisiblePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    if (lastVisiblePosition + 1 == recyclerView.getAdapter().getItemCount()) {
                        isLoad = true;
                    }
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    int spanCount = staggeredGridLayoutManager.getSpanCount();
                    int[] lastCompletelyVisibleItemPositions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(new int[spanCount]);
                    lastVisiblePosition = lastCompletelyVisibleItemPositions[0];
                    for (int i = 1; i < lastCompletelyVisibleItemPositions.length; i++) {
                        int tempLast = lastCompletelyVisibleItemPositions[i];
                        if (lastVisiblePosition > tempLast) {
                            lastVisiblePosition = tempLast;
                        }
                    }
                    if (lastVisiblePosition + spanCount == recyclerView.getAdapter().getItemCount()) {
                        isLoad = true;
                    }
                }
                if (isLoad) {
                    loadingComplete();
                    mOnLoadingListener.onLoading();
                }
            }
        }
    };

    /**
     * @return 当前是否在加载状态中
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 设置加载状态,
     */
    public void loadingComplete() {
        this.isLoading = false;
    }


    /**
     * 加载监听器
     */
    public interface OnLoadingListener {
        /**
         * 当达到加载的动作的要求时触发回调
         */
        void onLoading();
    }

}
