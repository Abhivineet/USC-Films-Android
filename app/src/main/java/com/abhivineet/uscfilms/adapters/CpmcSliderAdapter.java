package com.abhivineet.uscfilms.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.abhivineet.uscfilms.models.MediaCard;
import com.abhivineet.uscfilms.MediaDetailsActivity;
import com.abhivineet.uscfilms.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;


import java.util.ArrayList;

import androidx.annotation.Nullable;
import jp.wasabeef.glide.transformations.BlurTransformation;

//import androidx.constraintlayout.widget.ConstraintLayout;

public class CpmcSliderAdapter extends SliderViewAdapter<CpmcSliderAdapter.CpmcSliderAdapterViewHolder> {

    private ArrayList<MediaCard> mediaCards;
    private final SliderView sliderView;

    public CpmcSliderAdapter(ArrayList<MediaCard> mediaCards, SliderView sliderView) {
        this.mediaCards = mediaCards;
        this.sliderView = sliderView;
    }

    public void changeData(ArrayList<MediaCard> newList){
        this.mediaCards = newList;
        notifyDataSetChanged();
    }

    @Override
    public CpmcSliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cpmc_slider, null);
        return new CpmcSliderAdapterViewHolder(inflate, this.sliderView);
    }

    @Override
    public void onBindViewHolder(CpmcSliderAdapterViewHolder viewHolder, int position) {
        final MediaCard sliderItem = mediaCards.get(position);

//        Log.d("image path", sliderItem.getImagePath());

        if (sliderItem.getImagePath().equals("")){
            viewHolder.imageViewBackground.setImageResource(R.drawable.poster_path_placeholder);
            viewHolder.posterImage.setImageResource(R.drawable.poster_path_placeholder);
        } else {
            Glide.with(viewHolder.itemView)
                    .load(sliderItem.getImagePath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            viewHolder.imageViewBackground.setImageResource(R.drawable.poster_path_placeholder);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into(viewHolder.imageViewBackground);

            Glide.with(viewHolder.itemView)
                    .load(sliderItem.getImagePath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            viewHolder.posterImage.setImageResource(R.drawable.poster_path_placeholder);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(viewHolder.posterImage);
        }

        viewHolder.posterImage.setOnClickListener(view -> {
            viewHolder.sliderView.stopAutoCycle();
            Intent detailsIntent = new Intent(viewHolder.context, MediaDetailsActivity.class);
            detailsIntent.putExtra("Media", sliderItem);
            viewHolder.context.startActivity(detailsIntent);
            viewHolder.sliderView.startAutoCycle();
        });

    }

    @Override
    public int getCount() {
        return mediaCards.size();
    }

    static class CpmcSliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        View inflate;
        ImageView imageViewBackground;
        ImageView posterImage;
        FrameLayout layout;
        SliderView sliderView;
        private final Context context;

        public CpmcSliderAdapterViewHolder(View inflate, SliderView sliderView) {
            super(inflate);
            context = inflate.getContext();
            imageViewBackground = inflate.findViewById(R.id.cpmc_background);
            posterImage = inflate.findViewById(R.id.myImage);
            layout = inflate.findViewById(R.id.cpmc_frame);
            this.sliderView = sliderView;
            this.inflate = inflate;
        }
    }
}
