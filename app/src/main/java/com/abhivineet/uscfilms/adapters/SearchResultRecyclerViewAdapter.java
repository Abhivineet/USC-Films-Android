package com.abhivineet.uscfilms.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhivineet.uscfilms.MediaDetailsActivity;
import com.abhivineet.uscfilms.R;
import com.abhivineet.uscfilms.models.MediaCard;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResultRecyclerViewAdapter extends RecyclerView.Adapter{
    JSONArray searchResultsContent;

    public SearchResultRecyclerViewAdapter(JSONArray searchResultsContent) {
        this.searchResultsContent = searchResultsContent;
    }

    public void changeData(JSONArray jsonArray){
        this.searchResultsContent = new JSONArray();
        this.searchResultsContent = jsonArray;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_result_card, parent, false);
        return new SearchResultHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            JSONObject currentMediaObject = (JSONObject) searchResultsContent.get(position);
            if (currentMediaObject.getString("backdrop_path").equals("")){
                ((SearchResultHolder) holder).poster.setImageResource(R.drawable.backdrop_path_placeholder);
                try {
                    ((SearchResultHolder) holder).rating.setText(currentMediaObject.getString("rating"));
                    ((SearchResultHolder) holder).title.setText(currentMediaObject.getString("title"));
                    ((SearchResultHolder) holder).media.setText(currentMediaObject.getString("media_year"));
                    ((SearchResultHolder) holder).star.setVisibility(View.VISIBLE);
                } catch (Exception ignored){}
            }
            else {
                Glide.with(holder.itemView)
                        .load(currentMediaObject.getString("backdrop_path"))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                ((SearchResultHolder) holder).poster.setImageResource(R.drawable.backdrop_path_placeholder);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                try {
                                    ((SearchResultHolder) holder).rating.setText(currentMediaObject.getString("rating"));
                                    ((SearchResultHolder) holder).title.setText(currentMediaObject.getString("title"));
                                    ((SearchResultHolder) holder).media.setText(currentMediaObject.getString("media_year"));
                                    ((SearchResultHolder) holder).star.setVisibility(View.VISIBLE);
                                } catch (Exception ignored) {
                                }
                                return false;
                            }
                        })
                        .fitCenter()
                        .into(((SearchResultHolder) holder).poster);
            }
            Intent detailsIntent = new Intent(((SearchResultHolder) holder).context, MediaDetailsActivity.class);
            detailsIntent.putExtra("Media", new MediaCard(currentMediaObject.getString("media"), currentMediaObject.getString("title"), currentMediaObject.getString("backdrop_path"), currentMediaObject.getString("id")));
            ((SearchResultHolder) holder).frameLayout.setOnClickListener(view -> ((SearchResultHolder) holder).context.startActivity(detailsIntent));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return searchResultsContent.length();
    }

    static class SearchResultHolder extends RecyclerView.ViewHolder{
        private final TextView rating;
        private final TextView title;
        private final TextView media;
        private final ImageView poster;
        private final Context context;
        private final FrameLayout frameLayout;
        private final ImageButton star;

        public SearchResultHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            rating = itemView.findViewById(R.id.search_result_rating);
            title = itemView.findViewById(R.id.media_title);
            media = itemView.findViewById(R.id.media_year);
            poster = itemView.findViewById(R.id.search_poster);
            frameLayout = itemView.findViewById(R.id.search_poster_container);
            star = itemView.findViewById(R.id.search_result_star);
        }
    }

}
