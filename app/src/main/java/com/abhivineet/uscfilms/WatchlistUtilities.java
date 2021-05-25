package com.abhivineet.uscfilms;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.abhivineet.uscfilms.models.MediaCard;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class WatchlistUtilities {
    private final SharedPreferences sharedPreferences;
    private final String key;

    public WatchlistUtilities(Context context) {
        key = "watchlistPreferences";
        String sharedPreferencesName = "uscFilmsSharedPreferences";
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
    }

    public ArrayList<MediaCard> getWatchlist() throws JSONException {
        String watchlistString = sharedPreferences.getString(key, null);
        if (watchlistString == null){
            watchlistString = "[]";
        }
        JSONArray watchlistJSONArray = new JSONArray(watchlistString);
        return makeMediaCard(watchlistJSONArray);
    }

    public boolean isWatchlistEmpty(){
        try {
            ArrayList<MediaCard> watchlistCards =  getWatchlist();
            return watchlistCards.isEmpty();
        } catch(Exception ignored){}
        return true;
    }

    public boolean isInWatchlist(String id){
        try {
            ArrayList<MediaCard> watchlistCards = getWatchlist();
            for(int i=0; i<watchlistCards.size(); i++){
                if (watchlistCards.get(i).getId().equals(id)){
                    return true;
                }
            }
        } catch(Exception ignored){
        }
        return false;
    }

    public void addToWatchlist(MediaCard object){
        try {
            ArrayList<MediaCard> watchlistCards = getWatchlist();
            watchlistCards.add(object);
            writeSharedPreference(watchlistCards);
        } catch(Exception ignored){}
    }

    public void removeFromWatchlist(int pos){
        try {
            ArrayList<MediaCard> watchlistCards = getWatchlist();
            if(pos>=0 && pos<watchlistCards.size()) {
                watchlistCards.remove(pos);
                writeSharedPreference(watchlistCards);
            }
            else{
                Log.e("Woah", "Crash the app!");
            }
        } catch(Exception ignored){}
    }

    public void removeFromWatchlist(String id) {
        try {
            ArrayList<MediaCard> watchlistCards = getWatchlist();
            int pos = -1;
            for(int i=0; i<watchlistCards.size(); i++){
                if (watchlistCards.get(i).getId().equals(id)){
                    pos = i;
                    break;
                }
            }

            if(pos>=0 && pos<watchlistCards.size()) {
                watchlistCards.remove(pos);
                writeSharedPreference(watchlistCards);
            }
            else{
                Log.e("Woah", "Crash the app!");
            }
        } catch(Exception ignored){
        }
    }

    public void writeSharedPreference(ArrayList<MediaCard> cardList){
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(cardList.toArray());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, json);
        editor.apply();
    }

    private ArrayList<MediaCard> makeMediaCard(JSONArray content) throws JSONException {
        ArrayList<MediaCard> cardList = new ArrayList<>();
        for (int i = 0; i <content.length() ; i++) {
            JSONObject object = content.getJSONObject(i);
            cardList.add(new MediaCard(object.getString("media"), object.getString("title"), object.getString("imagePath"), object.getString("id")));
        }
        return cardList;
    }

}
