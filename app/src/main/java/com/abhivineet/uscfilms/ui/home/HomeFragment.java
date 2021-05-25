package com.abhivineet.uscfilms.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.abhivineet.uscfilms.adapters.CpmcSliderAdapter;
import com.abhivineet.uscfilms.MainActivity;
import com.abhivineet.uscfilms.decorations.MediaRecyclerDecoration;
import com.abhivineet.uscfilms.models.MediaCard;
import com.abhivineet.uscfilms.adapters.MediaRecyclerViewAdapter;
import com.abhivineet.uscfilms.R;
import com.abhivineet.uscfilms.ui.dashboard.DashboardViewModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ArrayList<MediaCard> mCpmcData;
    private ArrayList<MediaCard> mPopularData;
    private ArrayList<MediaCard> mTopRatedData;
    private ArrayList<MediaCard> tCpmcData;
    private ArrayList<MediaCard> tPopularData;
    private ArrayList<MediaCard> tTopRatedData;

    private RecyclerView topRatedRecyclerView;
    private RecyclerView popularRecyclerView;
    private MediaRecyclerViewAdapter topRatedRecyclerViewAdapter;
    private MediaRecyclerViewAdapter popularRecyclerViewAdapter;
    private TextView movieToggler;
    private TextView tvToggler;
    private SliderView sliderView;
    private CpmcSliderAdapter sliderAdapter;
    private ConstraintLayout spinnerHolder;
    private ConstraintLayout toolbar;
    private ScrollView scrollView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        scrollView = root.findViewById(R.id.text_home);
        spinnerHolder = root.findViewById(R.id.spinner_holder);
        toolbar = root.findViewById(R.id.header_container);

        movieToggler = root.findViewById(R.id.movies);
        tvToggler = root.findViewById(R.id.tv);
        TextView tmdb = root.findViewById(R.id.tmdb_text);

        tmdb.setOnClickListener(view -> getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/"))));

        topRatedRecyclerView = root.findViewById(R.id.top_rated_view);
        popularRecyclerView = root.findViewById(R.id.popular_view);
        LinearLayoutManager topRatedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topRatedRecyclerView.setLayoutManager(topRatedLayoutManager);
        popularRecyclerView.setLayoutManager(popularLayoutManager);

        topRatedRecyclerView.addItemDecoration(new MediaRecyclerDecoration(0, 45, 0,0));
        popularRecyclerView.addItemDecoration(new MediaRecyclerDecoration(0, 45, 0,0));

        sliderView = root.findViewById(R.id.cpmc_slider);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);

        RequestQueue queue = Volley.newRequestQueue(MainActivity.getContext());
        String url = MainActivity.getContext().getResources().getString(R.string.backend_host) + "api/home_data";
        StringRequest request = new StringRequest(Request.Method.GET, url, (Response.Listener<String>) response -> {
            try {
                JSONObject response_object = new JSONObject(response);
                mCpmcData = makeMediaCard(response_object.getJSONArray("cpmc"));
                mPopularData = makeMediaCard(response_object.getJSONArray("popular_movies"));
                mTopRatedData = makeMediaCard(response_object.getJSONArray("top_rated_movies"));
                tCpmcData = makeMediaCard(response_object.getJSONArray("trending_tv_shows"));
                tPopularData = makeMediaCard(response_object.getJSONArray("popular_tv_shows"));
                tTopRatedData = makeMediaCard(response_object.getJSONArray("top_rated_tv_shows"));

                sliderAdapter = new CpmcSliderAdapter(mCpmcData, sliderView);
                sliderView.setSliderAdapter(sliderAdapter);
                sliderView.startAutoCycle();

                topRatedRecyclerViewAdapter = new MediaRecyclerViewAdapter(mTopRatedData, false, false);
                popularRecyclerViewAdapter = new MediaRecyclerViewAdapter(mPopularData, false, false);

                topRatedRecyclerView.setAdapter(topRatedRecyclerViewAdapter);
                popularRecyclerView.setAdapter(popularRecyclerViewAdapter);

                dataFetched();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d("Error m8", "onErrorResponse: " + error));
        queue.add(request);

        movieToggler.setOnClickListener(view -> {
            movieToggler.setClickable(false);
            tvToggler.setTextColor(ContextCompat.getColor(MainActivity.getContext(), R.color.highlight));
            tvToggler.setClickable(true);
            movieToggler.setTextColor(Color.WHITE);
            sliderAdapter.changeData(mCpmcData);
            topRatedRecyclerViewAdapter.changeData(mTopRatedData);
            popularRecyclerViewAdapter.changeData(mPopularData);
        });

        tvToggler.setOnClickListener(view -> {
            tvToggler.setTextColor(Color.WHITE);
            tvToggler.setClickable(false);
            movieToggler.setClickable(true);
            movieToggler.setTextColor(ContextCompat.getColor(MainActivity.getContext(), R.color.highlight));
            sliderAdapter.changeData(tCpmcData);
            topRatedRecyclerViewAdapter.changeData(tTopRatedData);
            popularRecyclerViewAdapter.changeData(tPopularData);
        });

        return root;
    }

    private ArrayList<MediaCard> makeMediaCard(JSONArray content) throws JSONException {
        ArrayList<MediaCard> cardList = new ArrayList<>();
        for (int i = 0; i <content.length() ; i++) {
            JSONObject object = content.getJSONObject(i);
            cardList.add(new MediaCard(object.getString("media"), object.getString("title"), object.getString("image_path"), object.getString("id")));
        }
        return cardList;
    }

    private void dataFetched(){
        spinnerHolder.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
    }
}