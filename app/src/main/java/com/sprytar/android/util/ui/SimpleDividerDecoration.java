package com.sprytar.android.util.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sprytar.android.R;

public class SimpleDividerDecoration extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    public SimpleDividerDecoration(Context context) {
        this(context, R.drawable.line_divider);
    }

    public SimpleDividerDecoration(Context context, int drawable) {
        divider = context.getResources().getDrawable(drawable);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            drawBottom(c, child);
        }
    }

    private void drawBottom(Canvas c, View child) {
        int top = child.getBottom();
        int bottom = top + divider.getIntrinsicHeight();
        int left = child.getLeft();
        int right = child.getRight();
        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
    }
}
