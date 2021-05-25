package com.abhivineet.uscfilms.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abhivineet.uscfilms.R;
import com.abhivineet.uscfilms.adapters.SearchResultRecyclerViewAdapter;
import com.abhivineet.uscfilms.decorations.MediaRecyclerDecoration;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class DashboardFragment extends Fragment {

    private TextView placeHolder;
    private RecyclerView searchResultsRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        SearchView searchView = (SearchView) root.findViewById(R.id.search_view);
        placeHolder = (TextView) root.findViewById(R.id.search_result_placeholder);
        searchResultsRecyclerView = (RecyclerView) root.findViewById(R.id.search_result_recycler_view);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchResultsRecyclerView.addItemDecoration(new MediaRecyclerDecoration(20,20,20,20));
        final SearchResultRecyclerViewAdapter[] searchResultRecyclerViewAdapter = {null};

        searchResultsRecyclerView.setVisibility(View.GONE);
        placeHolder.setVisibility(View.GONE);

        searchView.requestFocus();

//        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(searchView, InputMethodManager.SHOW_FORCED);

        searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showInputMethod(view.findFocus());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()){
                    searchResultsRecyclerView.setVisibility(View.GONE);
                    placeHolder.setVisibility(View.GONE);
                    return false;
                }
                RequestQueue queue = Volley.newRequestQueue(requireContext());
                String url = getContext().getResources().getString(R.string.backend_host) + "api/search/" + s;
                StringRequest request = new StringRequest(Request.Method.GET, url, (Response.Listener<String>) response -> {
                    try {
                        Log.d("search_query", s);
                        Log.d("search_response", response);
                        JSONArray response_object = new JSONArray(response);
                        if (response_object.length()==0){
                            searchResultsRecyclerView.setVisibility(View.GONE);
                            placeHolder.setVisibility(View.VISIBLE);
                        }
                        else{
                            if (searchResultRecyclerViewAdapter[0] == null){
                                searchResultRecyclerViewAdapter[0] = new SearchResultRecyclerViewAdapter(response_object);
                                searchResultsRecyclerView.setAdapter(searchResultRecyclerViewAdapter[0]);
                            }
                            else{
                                searchResultRecyclerViewAdapter[0].changeData(response_object);
                            }
                            searchResultsRecyclerView.setVisibility(View.VISIBLE);
                            placeHolder.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d("Error m8", "onErrorResponse: " + error));
                queue.add(request);
                return false;
            }
        });

//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }
}