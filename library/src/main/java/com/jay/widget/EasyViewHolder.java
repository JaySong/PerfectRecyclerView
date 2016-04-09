package com.jay.widget;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created on 2016/4/9
 *
 * @author Q.Jay
 * @version 1.0.0
 */
public class EasyViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views = new SparseArray<>();
    public EasyViewHolder(View itemView) {
        super(itemView);
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (V) view;
    }


}
