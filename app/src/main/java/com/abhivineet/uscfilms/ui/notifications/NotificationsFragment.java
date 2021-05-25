package com.abhivineet.uscfilms.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.abhivineet.uscfilms.MainActivity;
import com.abhivineet.uscfilms.decorations.WatchlistRecyclerDecoration;
import com.abhivineet.uscfilms.models.MediaCard;
import com.abhivineet.uscfilms.adapters.MediaRecyclerViewAdapter;
import com.abhivineet.uscfilms.WatchlistUtilities;

import com.abhivineet.uscfilms.R;
import com.abhivineet.uscfilms.ui.dashboard.DashboardViewModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    WatchlistUtilities watchlistUtilities;
    TextView placeholderTextView;
    RecyclerView watchlistRecyclerView;
    MediaRecyclerViewAdapter watchlistRecyclerViewAdapter;
    GridLayoutManager watchlistLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        watchlistRecyclerView = (RecyclerView) root.findViewById(R.id.watchlist_grid);
        watchlistLayoutManager = new GridLayoutManager(MainActivity.getContext(), 3);
        watchlistRecyclerView.addItemDecoration(new WatchlistRecyclerDecoration(50));
        watchlistRecyclerView.setLayoutManager(watchlistLayoutManager);
        placeholderTextView = (TextView) root.findViewById(R.id.watchlist_placeholder);
        try {
            watchlistUtilities = new WatchlistUtilities(requireContext());
            if (watchlistUtilities.isWatchlistEmpty()){
                placeholderTextView.setVisibility(View.VISIBLE);
            }
            else{
                ArrayList<MediaCard> watchlistCards = watchlistUtilities.getWatchlist();
                watchlistRecyclerViewAdapter = new MediaRecyclerViewAdapter(watchlistCards, false, true);
                watchlistRecyclerViewAdapter.setWatchlistRecyclerView(watchlistRecyclerView);
                watchlistRecyclerViewAdapter.setPlaceholderTextView(placeholderTextView);

                watchlistRecyclerView.setAdapter(watchlistRecyclerViewAdapter);
                watchlistRecyclerView.setVisibility(View.VISIBLE);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(watchlistRecyclerView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (watchlistRecyclerViewAdapter!=null) {
                watchlistRecyclerViewAdapter.refreshWatchlist();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int cardTouched = viewHolder.getAdapterPosition();
            int cardDroppedOn = target.getAdapterPosition();

            try {
                ArrayList<MediaCard> watchlist = watchlistUtilities.getWatchlist();
                move(watchlist, cardTouched, cardDroppedOn);
                watchlistUtilities.writeSharedPreference(watchlist);
                watchlistRecyclerViewAdapter.notifyItemMoved(cardTouched, cardDroppedOn);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    public void move(List<MediaCard> myList, int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            myList.add(toPosition, myList.get(fromPosition));
            myList.remove(fromPosition);
        } else {
            if (fromPosition!=toPosition){
                myList.add(toPosition, myList.get(fromPosition));
                myList.remove(fromPosition + 1);
            }
        }
    }
}