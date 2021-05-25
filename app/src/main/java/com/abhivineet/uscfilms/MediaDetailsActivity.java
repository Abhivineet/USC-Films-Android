package com.abhivineet.uscfilms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhivineet.uscfilms.adapters.CastCardAdapter;
import com.abhivineet.uscfilms.adapters.MediaRecyclerViewAdapter;
import com.abhivineet.uscfilms.adapters.ReviewCardRecyclerViewAdapter;
import com.abhivineet.uscfilms.decorations.MediaRecyclerDecoration;
import com.abhivineet.uscfilms.models.CastCard;
import com.abhivineet.uscfilms.models.MediaCard;
import com.abhivineet.uscfilms.models.ReviewCard;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.abhivineet.uscfilms.MainActivity.getContext;

public class MediaDetailsActivity extends AppCompatActivity {

    private String id;
    private String title;
    private String imagePath;
    private String videoId;
    private String overview;
    private String genres;
    private String year;
    private ArrayList<CastCard> cast;
    private ArrayList<MediaCard> recommendedMedia;
    private ArrayList<ReviewCard> reviews;
    private String facebookLink;
    private String twitterLink;

    private ScrollView scrollView;
    private ConstraintLayout spinnerHolder;
    private YouTubePlayerView youTubePlayerView;
    private ImageView fallbackBackdrop;
    private TextView titleHolder;
    private TextView overviewHeader;
    private ReadMoreTextView overviewContent;
    private TextView yearHeader;
    private TextView yearContent;
    private TextView genresHeader;
    private TextView genresContent;
    private ImageButton watchlistToggler;
    private ImageButton facebookShare;
    private ImageButton twitterShare;
    private TextView castHeader;
    private RecyclerView castGrid;
    private CastCardAdapter castCardAdapter;
    private TextView reviewHeader;
    private RecyclerView reviewRecyclerView;
    private ReviewCardRecyclerViewAdapter reviewCardRecyclerViewAdapter;
    private TextView recommendedHeader;
    private RecyclerView recommendedRecyclerView;
    private MediaRecyclerViewAdapter recommendedRecyclerViewAdapter;

    private WatchlistUtilities watchlistUtilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_media_details);

        scrollView = (ScrollView) findViewById(R.id.media_details_scroll_view);
        spinnerHolder = (ConstraintLayout) findViewById(R.id.media_details_spinner_holder);

        titleHolder = (TextView) findViewById(R.id.title_holder);
        overviewHeader = (TextView) findViewById(R.id.overview_header);
        yearHeader = (TextView) findViewById(R.id.year_header);
        genresHeader = (TextView) findViewById(R.id.genres_header);
        castHeader = (TextView) findViewById(R.id.cast_header);
        reviewHeader = (TextView) findViewById(R.id.reviews_header);
        recommendedHeader = (TextView) findViewById(R.id.recommended_header);

        overviewContent = (ReadMoreTextView) findViewById(R.id.overview_content);
        yearContent = (TextView) findViewById(R.id.year_content);
        genresContent = (TextView) findViewById(R.id.genres_content);

        watchlistToggler = (ImageButton) findViewById(R.id.watchlist_toggler);
        facebookShare = (ImageButton) findViewById(R.id.facebook_share);
        twitterShare = (ImageButton) findViewById(R.id.twitter_share);

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        fallbackBackdrop = (ImageView) findViewById(R.id.fallback_image);


        castGrid = (RecyclerView) findViewById(R.id.cast_grid);
        castGrid.setNestedScrollingEnabled(false);
        GridLayoutManager castGridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);

        reviewRecyclerView = (RecyclerView) findViewById(R.id.reviews_content);
        reviewRecyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager reviewsRecyclerViewLayoutManager = new GridLayoutManager(getApplicationContext(), 1);

        recommendedRecyclerView = (RecyclerView) findViewById(R.id.recommended_content);
        LinearLayoutManager recommendedRecyclerViewLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);


        Intent i = getIntent();
        MediaCard mediaCardReceived = (MediaCard) i.getSerializableExtra("Media");
        title = mediaCardReceived.getTitle();
        String media = mediaCardReceived.getMedia();
        id = mediaCardReceived.getId();

//        Log.d("id", id);


        watchlistUtilities = new WatchlistUtilities(getContext());
        if (watchlistUtilities.isInWatchlist(id)){
            watchlistToggler.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
        }
        else{
            watchlistToggler.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
        }

        watchlistToggler.setOnClickListener(view -> {
            if (watchlistUtilities.isInWatchlist(id)){
                watchlistUtilities.removeFromWatchlist(id);
                watchlistToggler.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
                Toast.makeText(
                        getContext(),
                        mediaCardReceived.getTitle() + " was removed from Watchlist",
                        Toast.LENGTH_LONG
                ).show();
            }
            else{
                watchlistUtilities.addToWatchlist(mediaCardReceived);
                watchlistToggler.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
                Toast.makeText(
                        getContext(),
                        mediaCardReceived.getTitle() + " was added to Watchlist",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = getContext().getResources().getString(R.string.backend_host) + "api/details/" + media + "/" + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, (Response.Listener<String>) response -> {
            try {
                JSONObject response_object = new JSONObject(response);
                imagePath = response_object.getString("image_path");
                if (response_object.getString("video_present").equals("true")){
                    fallbackBackdrop.setVisibility(View.GONE);

                    getLifecycle().addObserver(youTubePlayerView);
                    videoId = response_object.getString("video_key");
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            youTubePlayer.cueVideo(videoId, 0);
                        }
                    });

                    youTubePlayerView.setVisibility(View.VISIBLE);

                }
                else{
                    if (imagePath.equals("")){
                        fallbackBackdrop.setImageResource(R.drawable.backdrop_path_placeholder);
                    }
                    else {
                        Glide.with(this)
                                .load(imagePath)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        fallbackBackdrop.setImageResource(R.drawable.backdrop_path_placeholder);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .fitCenter()
                                .into(fallbackBackdrop);
                    }

                    youTubePlayerView.setVisibility(View.GONE);
                    fallbackBackdrop.setVisibility(View.VISIBLE);
                }

                title = response_object.getString("title");
                overview = response_object.getString("overview");
                genres = response_object.getString("genres");
                year = response_object.getString("year");

                if (!response_object.getString("cast_list_length").equals("0")){
                    cast = makeCastCard(new JSONArray(response_object.getString("cast_list")));
                    castCardAdapter = new CastCardAdapter(cast);
                    castGrid.setLayoutManager(castGridLayoutManager);
                    castGrid.setAdapter(castCardAdapter);
                }
                else{
                    castHeader.setVisibility(View.GONE);
                    castGrid.setVisibility(View.GONE);
                }
                if (!response_object.getString("recommended_picks_length").equals("0")) {
                    recommendedMedia = makeMediaCard(new JSONArray(response_object.getString("recommended_picks")));
                    recommendedRecyclerViewAdapter = new MediaRecyclerViewAdapter(recommendedMedia, true, false);
                    recommendedRecyclerView.setAdapter(recommendedRecyclerViewAdapter);
                    recommendedRecyclerView.setLayoutManager(recommendedRecyclerViewLayoutManager);
                    recommendedRecyclerView.addItemDecoration(new MediaRecyclerDecoration(0, 45, 0,100));
                }
                else{
                    recommendedHeader.setVisibility(View.GONE);
                    recommendedRecyclerView.setVisibility(View.GONE);
                }

                if (!response_object.getString("reviews_list_length").equals("0")){
                    reviews = makeReviewCard(new JSONArray(response_object.getString("reviews_list")));
                    reviewCardRecyclerViewAdapter = new ReviewCardRecyclerViewAdapter(reviews);
                    reviewRecyclerView.setAdapter(reviewCardRecyclerViewAdapter);
                    reviewRecyclerView.setLayoutManager(reviewsRecyclerViewLayoutManager);
                }
                else{
                    reviewHeader.setVisibility(View.GONE);
                    reviewRecyclerView.setVisibility(View.GONE);
                }

                facebookLink = response_object.getString("facebook");
                twitterLink = response_object.getString("twitter");

                facebookShare.setOnClickListener(view -> getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookLink))));

                twitterShare.setOnClickListener(view -> getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterLink))));

//                Log.d("cast length", response_object.getString("cast_list_length"));
//                Log.d("reco length", response_object.getString("recommended_picks_length"));
//                Log.d("review length", response_object.getString("reviews_list_length"));

//                Log.d("title", title);
                titleHolder.setText(title);

                if (overview.equals("")){
                    overviewHeader.setVisibility(View.GONE);
                    overviewContent.setVisibility(View.GONE);
                }else {
                    overviewContent.setText(overview);
                }

                if (year.equals("")){
                    yearHeader.setVisibility(View.GONE);
                    yearContent.setVisibility(View.GONE);
                }else {
                    yearContent.setText(year);
                }

                if (genres.equals("")){
                    genresHeader.setVisibility(View.GONE);
                    genresContent.setVisibility(View.GONE);
                }else {
                    genresContent.setText(genres);
                }

                onDataFetched();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d("Error m8", "onErrorResponse: " + error));
        queue.add(request);
    }

    private ArrayList<MediaCard> makeMediaCard(JSONArray content) throws JSONException {
        ArrayList<MediaCard> cardList = new ArrayList<>();
        for (int i = 0; i <content.length() ; i++) {
            JSONObject object = content.getJSONObject(i);
            cardList.add(new MediaCard(object.getString("media"), object.getString("title"), object.getString("image_path"), object.getString("id")));
//            Log.d("media", cardList.get(cardList.size()-1).toString());
        }
        return cardList;
    }

    private ArrayList<ReviewCard> makeReviewCard(JSONArray content) throws JSONException {
        ArrayList<ReviewCard> cardList = new ArrayList<>();
        for (int i = 0; i <content.length() ; i++) {
            JSONObject object = content.getJSONObject(i);
            cardList.add(new ReviewCard(object.getString("author"), object.getString("content"), object.getString("created_at"), object.getString("rating")));
//            Log.d("review", cardList.get(cardList.size()-1).toString());
        }
        return cardList;
    }

    private ArrayList<CastCard> makeCastCard(JSONArray content) throws JSONException{
        ArrayList<CastCard> cardList = new ArrayList<>();
        for (int i = 0; i<content.length(); i++){
            JSONObject object = content.getJSONObject(i);
            cardList.add(new CastCard(object.getString("name"), object.getString("profile_path")));
//            Log.d("cast", cardList.get(cardList.size()-1).toString());
        }
        return cardList;
    }

    private void onDataFetched(){
        spinnerHolder.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

}