package com.jay.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 2016/4/9
 *
 * @author Q.Jay
 * @version 1.0.0
 */
public class GroupRecyclerView extends SimpleRecyclerView implements RecyclerView.OnItemTouchListener {
    private GestureDetector mGestureDetector;
    private OnGroupItemLongClickListener mOnGroupItemLongClickListener;
    private OnChildItemLongClickListener mOnChildItemLongClickListener;
    private OnGroupItemClickListener mOnGroupItemClickListener;
    private OnChildItemClickListener mOnChildItemClickListener;

    public GroupRecyclerView(Context context) {
        super(context);
    }

    public GroupRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init(Context context) {
        super.init(context);
        mGestureDetector = new GestureDetector(context, new ItemClickGestureDetector());
        addOnItemTouchListener(this);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mGestureDetector.onTouchEvent(e)) {

            int childLayoutPosition = getChildLayoutPosition(childView);
            GroupAdapter adapter = (GroupAdapter) getAdapter();

            boolean isGroup = adapter.isGroup(childLayoutPosition);
            if (isGroup) {
                if (mOnGroupItemClickListener != null) {
                    mOnGroupItemClickListener.onGroupItemClick(this, adapter, childView, adapter.getGroupPosition(childLayoutPosition));
                }
            } else {
                if (mOnChildItemClickListener != null) {
                    int groupPosition = adapter.getGroupPosition(childLayoutPosition);
                    int childPosition = adapter.getChildPosition(childLayoutPosition);
                    mOnChildItemClickListener.onChildItemClick(this, adapter, childView, groupPosition, childPosition);
                }
            }
            return true;
        }
        return false;
    }

    public void setOnGroupItemClickListener(OnGroupItemClickListener listener) {
        mOnGroupItemClickListener = listener;
    }

    public void setOnChildItemClickListener(OnChildItemClickListener listener) {
        mOnChildItemClickListener = listener;
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
            if (childView != null) {
                final int childLayoutPosition = GroupRecyclerView.this.getChildLayoutPosition(childView);
                GroupAdapter adapter = (GroupAdapter) getAdapter();
                boolean isGroup = adapter.isGroup(childLayoutPosition);
                if (isGroup) {
                    if (mOnGroupItemLongClickListener != null) {
                        int groupPosition = adapter.getGroupPosition(childLayoutPosition);
                        mOnGroupItemLongClickListener.onGroupItemLongClick(GroupRecyclerView.this, adapter, childView, groupPosition);
                    }
                } else {
                    if (mOnChildItemLongClickListener != null) {
                        int groupPosition = adapter.getGroupPosition(childLayoutPosition);
                        int childPosition = adapter.getChildPosition(childLayoutPosition);
                        mOnChildItemLongClickListener.onChildItemLongClick(GroupRecyclerView.this, adapter, childView, groupPosition, childPosition);
                    }
                }
            }
        }
    }

    public void setOnGroupItemLongClickListener(OnGroupItemLongClickListener l) {
        mOnGroupItemLongClickListener = l;
    }

    public void setOnChildItemLongClickListener(OnChildItemLongClickListener l) {
        mOnChildItemLongClickListener = l;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof GroupAdapter)) {
            throw new IllegalArgumentException("adapter must extends class GroupAdapter");
        }
        super.setAdapter(adapter);
    }

    public static abstract class GroupAdapter extends RecyclerView.Adapter<EasyViewHolder> {
        private static final int GROUP_ITEM = -1;
        private static final int CHILD_ITEM = -2;
        /**
         * 用于记录当前组在列表中的序列位置
         */
        private int tempGroupPosition;

        private SparseArray<Integer> mGroupIndex = new SparseArray<>();
        private SparseArray<Integer> mGroupChildIndexMap = new SparseArray<>();

        @Override
        public final EasyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (GROUP_ITEM == viewType) {
                return onCreateGroupViewHolder(parent, viewType);
            }
            return onCreateChildViewHolder(parent, viewType);
        }

        public abstract EasyViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType);

        public abstract EasyViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType);

        @Override
        public final void onBindViewHolder(EasyViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (GROUP_ITEM == viewType) {
                onBindGroupViewHolder(holder, getGroupPosition(position));
            } else {
                onBindChildViewHolder(holder, getGroupPosition(position), getChildPosition(position));
            }
        }

        public int getGroupPosition(int position) {
            return mGroupIndex.indexOfKey(getGroupIndex(position));
        }

        public int getGroupIndex(int position) {
            if (isGroup(position)) {
                tempGroupPosition = position;
                return position;
            }
            Integer integer = mGroupChildIndexMap.get(position);
            int groupPosition;
            if (integer == null) {
                groupPosition = tempGroupPosition;
                mGroupChildIndexMap.put(position, tempGroupPosition);
            } else {
                groupPosition = integer;
            }
            return groupPosition;
        }

        public int getChildPosition(int position) {
            return position - getGroupIndex(position) - 1;
        }

        public abstract void onBindGroupViewHolder(EasyViewHolder holder, int groupPosition);

        public abstract void onBindChildViewHolder(EasyViewHolder holder, int groupPosition, int childPosition);

        @Override
        public final int getItemCount() {
            final int groupCount = getGroupCount();
            int itemCount = 0;
            for (int i = 0; i < groupCount; i++) {
                mGroupIndex.put(itemCount, i);
                itemCount += getChildrenCount(groupCount) + 1;
            }
            return itemCount;
        }

        @Override
        public final int getItemViewType(int position) {
            if (isGroup(position)) {
                return GROUP_ITEM;
            }
            return CHILD_ITEM;
        }

        public boolean isGroup(int position) {
            return mGroupIndex.get(position) != null;
        }
        @Override
        public void onViewAttachedToWindow(EasyViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && isGroup(holder.getLayoutPosition())) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        public abstract int getGroupCount();

        public abstract int getChildrenCount(int groupPosition);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof GridLayoutManager) {
            final GridLayoutManager glm = (GridLayoutManager) layout;
            final GroupAdapter adapter = (GroupAdapter) getAdapter();
            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return adapter.isGroup(position) ? glm.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 条目点击事件监听
     */
    public interface OnGroupItemClickListener {
        /**
         * 当条目被点击时回调
         *
         * @param rv            {@link GroupRecyclerView}
         * @param adapter       {@link RecyclerView.Adapter}
         * @param view          受理触摸点击事件的View
         * @param groupPosition 点击条目的位置
         */
        void onGroupItemClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition);
    }

    /**
     * 条目点击事件监听
     */
    public interface OnChildItemClickListener {
        /**
         * 当条目被点击时回调
         *
         * @param rv            {@link GroupRecyclerView}
         * @param adapter       {@link RecyclerView.Adapter}
         * @param view          受理触摸点击事件的View
         * @param groupPosition 点击条目的位置
         */
        void onChildItemClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition, int childPosition);
    }

    /**
     * 子条目长按事件监听
     */
    public interface OnChildItemLongClickListener {
        /**
         * 当条目被长按点击时回调
         *
         * @param rv            {@link EasyRecyclerView}
         * @param adapter       {@link android.support.v7.widget.RecyclerView.Adapter}
         * @param view          受理触摸点击事件的View
         * @param groupPosition 点击组条目的位置
         * @param childPosition 点击某一组子条目的位置
         */
        void onChildItemLongClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition, int childPosition);
    }

    /**
     * 组条目长按事件监听
     */
    public interface OnGroupItemLongClickListener {
        /**
         * 当条目被长按点击时回调
         *
         * @param rv            {@link EasyRecyclerView}
         * @param adapter       {@link android.support.v7.widget.RecyclerView.Adapter}
         * @param view          受理触摸点击事件的View
         * @param groupPosition 点击条目的位置
         */
        void onGroupItemLongClick(GroupRecyclerView rv, GroupRecyclerView.GroupAdapter adapter, View view, int groupPosition);
    }
}
