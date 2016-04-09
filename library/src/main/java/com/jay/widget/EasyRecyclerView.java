package com.jay.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created on 2016/4/9
 *
 * @author Q.Jay
 * @version 1.0.0
 */
public class EasyRecyclerView extends SimpleRecyclerView implements RecyclerView.OnItemTouchListener {
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();

    private GestureDetector mGestureDetector;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemClickListener mOnItemClickListener;

    public EasyRecyclerView(Context context) {
        super(context);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init(Context context) {
        super.init(context);
        mGestureDetector = new GestureDetector(context, new ItemClickGestureDetector());
        addOnItemTouchListener(this);
    }

    //----------------------------------------------------------------------------------------------
    //header view and footer view code
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    public View getHeaderView(int index) {
        try {
            return mHeaderViews.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void clearHeaderView() {
        mHeaderViews.clear();
    }

    public void addFooterView(View view) {
        int size = mFooterViews.size();
        if (size > 0) {
            mFooterViews.add(mFooterViews.size() - 1, view);
        } else {
            mFooterViews.add(view);
        }
    }

    public View getFooterView(int index) {
        try {
            return mFooterViews.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void clearFooterView() {
        mFooterViews.clear();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViews.size() > 0 || mFooterViews.size() > 0) {
            WrapAdapter mWrapAdapter = new WrapAdapter(mHeaderViews, mFooterViews, adapter);
            super.setAdapter(mWrapAdapter);
        }else{
            super.setAdapter(adapter);
        }
    }



    class WrapAdapter extends RecyclerView.Adapter<EasyViewHolder> {

        private static final int HEADER_VIEW_TYPE = Integer.MAX_VALUE;
        private static final int FOOTER_VIEW_TYPE = Integer.MIN_VALUE;

        private final Adapter mAdapter;

        private int currentHeaderPosition;
        private int currentFooterPosition;

        public WrapAdapter(ArrayList<View> headerViews, ArrayList<View> footerViews, Adapter adapter) {
            mAdapter = adapter;
            mHeaderViews = headerViews;
            mFooterViews = footerViews;
        }

        /**
         * @return 返回 Header View 数量
         */
        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        /**
         * @return 返回 Footer View 数量
         */
        public int getFootersCount() {
            return mFooterViews.size();
        }

        @Override
        public EasyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case HEADER_VIEW_TYPE:
                    return new EasyViewHolder(mHeaderViews.get(currentHeaderPosition++));
                case FOOTER_VIEW_TYPE:
                    return new EasyViewHolder(mHeaderViews.get(currentFooterPosition++));
            }
            return (EasyViewHolder) mAdapter.onCreateViewHolder(parent, viewType);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(EasyViewHolder holder, int position) {
            int bodyPosition = isBodyViewType(position);
            if (bodyPosition != -1) {
                mAdapter.onBindViewHolder(holder, bodyPosition);
            }
        }

        @Override
        public int getItemViewType(int position) {
            final int numHeaders = getHeadersCount();
            final int bodyPosition = isBodyViewType(position);
            if (bodyPosition != -1) {
                return mAdapter.getItemViewType(bodyPosition);
            }
            return position < numHeaders ? HEADER_VIEW_TYPE : FOOTER_VIEW_TYPE;
        }

        /**
         * 判断是否非 Header View or Footer View,返回-1表示非BodyView
         *
         * @param position 位置
         */
        private int isBodyViewType(int position) {
            int numHeaders = getHeadersCount();
            if (mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adjPosition;
                }
            }
            return -1;
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getFootersCount() + getHeadersCount() + mAdapter.getItemCount();
            } else {
                return getFootersCount() + getHeadersCount();
            }
        }

        public long getItemId(int position) {
            int numHeaders = getHeadersCount();
            if (mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getItemViewType(position) == HEADER_VIEW_TYPE || getItemViewType(position) == FOOTER_VIEW_TYPE ?
                                gridManager.getSpanCount():1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(EasyViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        /**
         * 判断当前位置是否为HeaderView
         *
         * @param position 当前条目位置
         */
        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        /**
         * 判断当前位置是否为FooterView
         *
         * @param position 当前条目位置
         */
        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFooterViews.size();
        }
    }

    //----------------------------------------------------------------------------------------------
    //click listener code
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mOnItemClickListener != null && mGestureDetector.onTouchEvent(e)) {
            // 触发单击事件
            mOnItemClickListener.onItemClick(
                    (EasyRecyclerView) rv,
                    getAdapter(),
                    childView,
                    rv.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    class ItemClickGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // 根据findChildViewUnder(float x, float y)来算出哪个item被选择了
            View childView = findChildViewUnder(e.getX(), e.getY());
            // 有item被选择且监听器不为空触发长按事件
            if (childView != null && mOnItemLongClickListener != null) {
                final int childLayoutPosition = EasyRecyclerView.this.getChildLayoutPosition(childView);
                mOnItemLongClickListener.onItemLongClick(EasyRecyclerView.this, getAdapter(), childView, childLayoutPosition);
            }
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        mOnItemLongClickListener = l;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    /**
     * 条目点击事件监听
     */
    public interface OnItemClickListener {
        /**
         * 当条目被点击时回调
         *
         * @param rv       {@link EasyRecyclerView}
         * @param adapter  {@link RecyclerView.Adapter}
         * @param view     受理触摸点击事件的View
         * @param position 点击条目的位置
         */
        void onItemClick(EasyRecyclerView rv, RecyclerView.Adapter adapter, View view, int position);
    }

    /**
     * 条目长按事件监听
     */
    public interface OnItemLongClickListener {
        /**
         * 当条目被长按点击时回调
         *
         * @param rv       {@link EasyRecyclerView}
         * @param adapter  {@link android.support.v7.widget.RecyclerView.Adapter}
         * @param view     受理触摸点击事件的View
         * @param position 点击条目的位置
         */
        void onItemLongClick(EasyRecyclerView rv, RecyclerView.Adapter adapter, View view, int position);
    }
}
