package ru.hookahlocator.hooklocator.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */

/**
 * extended for offscreen preloading recyclerviews
 */
public class ExtendedLinearLayoutManager extends LinearLayoutManager {
    public ExtendedLinearLayoutManager(Context context) {
        super(context);
    }

    public ExtendedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ExtendedLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return getHeight();
    }
}
