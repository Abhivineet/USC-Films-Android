package com.abhivineet.uscfilms.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.abhivineet.uscfilms.MainActivity;
import com.abhivineet.uscfilms.models.MediaCard;
import com.abhivineet.uscfilms.MediaDetailsActivity;
import com.abhivineet.uscfilms.R;
import com.abhivineet.uscfilms.WatchlistUtilities;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MediaRecyclerViewAdapter extends RecyclerView.Adapter{
    private ArrayList<MediaCard> cardList;
    private final boolean isRecommended;
    private final boolean isWatchlist;
    private final WatchlistUtilities watchlistUtilities;
    private RecyclerView watchlistRecyclerView;
    private TextView placeholderTextView;

    public void setWatchlistRecyclerView(RecyclerView watchlistRecyclerView) {
        this.watchlistRecyclerView = watchlistRecyclerView;
    }

    public void setPlaceholderTextView(TextView placeholderTextView) {
        this.placeholderTextView = placeholderTextView;
    }

    public void refreshWatchlist() throws JSONException {
        this.cardList = watchlistUtilities.getWatchlist();
        notifyDataSetChanged();
        if (cardList.size()==0){
            watchlistRecyclerView.setVisibility(View.GONE);
            placeholderTextView.setVisibility(View.VISIBLE);
        }
    }

    public MediaRecyclerViewAdapter(ArrayList<MediaCard> cardList, boolean isRecommended, boolean isWatchlist) {
        super();
        this.cardList = cardList;
        this.isRecommended = isRecommended;
        this.isWatchlist = isWatchlist;
        watchlistUtilities = new WatchlistUtilities(MainActivity.getContext());
    }

    public void changeData(ArrayList<MediaCard> newList){
        this.cardList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_card, parent, false);
        return new MediaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MediaCard mediaCardItem = this.cardList.get(position);

        if (mediaCardItem.getImagePath().equals("")){
            ((MediaHolder) holder).posterImageView.setImageResource(R.drawable.poster_path_placeholder);
        }
        else {
            Glide.with(holder.itemView)
                    .load(mediaCardItem.getImagePath())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((MediaHolder) holder).posterImageView.setImageResource(R.drawable.poster_path_placeholder);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(((MediaHolder) holder).posterImageView);
        }

        ((MediaHolder) holder).posterHolderLayout.setOnClickListener(view -> {
            Intent detailsIntent = new Intent(((MediaHolder) holder).context, MediaDetailsActivity.class);
            detailsIntent.putExtra("Media", mediaCardItem);
            ((MediaHolder) holder).context.startActivity(detailsIntent);
        });


        TextView showMoreButton = ((MediaHolder) holder).showMoreButton;
        ImageView imageOverlay = ((MediaHolder) holder).posterOverlayImageView;
        ImageButton removeButton = ((MediaHolder) holder).removeButton;
        TextView mediaType = ((MediaHolder) holder).mediaType;

        if (isRecommended){
            showMoreButton.setVisibility(View.GONE);
            imageOverlay.setVisibility(View.GONE);
            removeButton.setVisibility(View.GONE);
            mediaType.setVisibility(View.GONE);

        }
        else if (isWatchlist){
            showMoreButton.setVisibility(View.GONE);
            if (mediaCardItem.getMedia().equals("movie")){
                mediaType.setText(R.string.movie_placeholder);
            }
            else{
                mediaType.setText(R.string.tv_placeholder);
            }
            removeButton.setOnClickListener(view -> {
                cardList.remove(position);
                notifyDataSetChanged();
                if (cardList.size()==0){
                    watchlistRecyclerView.setVisibility(View.GONE);
                    placeholderTextView.setVisibility(View.VISIBLE);
                }
                watchlistUtilities.removeFromWatchlist(position);
                Toast.makeText(
                        removeButton.getContext(),
                        "\""+mediaCardItem.getTitle()+"\"" + " was removed from Watchlist",
                        Toast.LENGTH_LONG
                ).show();
            });
        }
        else{
            removeButton.setVisibility(View.GONE);
            mediaType.setVisibility(View.GONE);
            showMoreButton.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(showMoreButton.getContext(), showMoreButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                Menu menuOpts = popup.getMenu();
                if (watchlistUtilities.isInWatchlist(mediaCardItem.getId())){
                    menuOpts.getItem(3).setTitle("Remove from Watchlist");
                }
                popup.setOnMenuItemClickListener(item -> {
                    switch(String.valueOf(item.getTitle())){
                        case "Open in TMDB":
                            showMoreButton.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/" + mediaCardItem.getMedia() + "/" + mediaCardItem.getId())));
                            break;
                        case "Share on Twitter":
                            showMoreButton.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=" + Uri.encode("Check this out!") + "&url=" + Uri.encode("https://www.themoviedb.org/" + mediaCardItem.getMedia() + "/" + mediaCardItem.getId()))));
                            break;
                        case "Share on Facebook":
                            showMoreButton.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sharer/sharer.php?u=https://www.themoviedb.org/" + mediaCardItem.getMedia() + "/" + mediaCardItem.getId())));
                            break;
                        case "Add to Watchlist":
                            watchlistUtilities.addToWatchlist(mediaCardItem);
                            Toast.makeText(
                                    showMoreButton.getContext(),
                                    mediaCardItem.getTitle() + " was added to Watchlist",
                                    Toast.LENGTH_LONG
                            ).show();
                            break;
                        case "Remove from Watchlist":
                            watchlistUtilities.removeFromWatchlist(position);
                            Toast.makeText(
                                    showMoreButton.getContext(),
                                    mediaCardItem.getTitle() + " was removed from Watchlist",
                                    Toast.LENGTH_LONG
                            ).show();
                            break;
                    }

                    return true;
                });
                popup.show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class MediaHolder extends RecyclerView.ViewHolder{

        private final ImageView posterImageView;
        private final ImageView posterOverlayImageView;
        private final FrameLayout posterHolderLayout;
        private final Context context;
        private final TextView showMoreButton;
        private final TextView mediaType;
        private final ImageButton removeButton;

        public MediaHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.posterImageView = (ImageView) itemView.findViewById(R.id.poster);
            this.posterOverlayImageView = (ImageView) itemView.findViewById(R.id.poster_overlay);
            this.posterHolderLayout = (FrameLayout) itemView.findViewById(R.id.poster_container);
            this.showMoreButton = (TextView) itemView.findViewById(R.id.ellipsis);
            this.removeButton = (ImageButton) itemView.findViewById(R.id.watchlist_button);
            this.mediaType = (TextView) itemView.findViewById(R.id.media_type);
        }

    }
}
