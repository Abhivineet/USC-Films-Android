package com.abhivineet.uscfilms.decorations;

import android.graphics.Rect;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import androidx.recyclerview.widget.RecyclerView;

public class MediaRecyclerDecoration extends RecyclerView.ItemDecoration {
    private final int left;
    private final int right;
    private final int top;
    private final int bottom;

    public MediaRecyclerDecoration(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state){
        outRect.left = left;
        outRect.right = right;
        outRect.top = top;
        outRect.bottom = bottom;
    }
}
