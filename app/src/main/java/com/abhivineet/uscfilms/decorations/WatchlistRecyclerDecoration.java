package com.abhivineet.uscfilms.decorations;

import android.graphics.Rect;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import androidx.recyclerview.widget.RecyclerView;

public class WatchlistRecyclerDecoration extends RecyclerView.ItemDecoration {
    private int bottom;

    public WatchlistRecyclerDecoration(int bottom) {
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state){
        outRect.bottom = bottom;
    }
}