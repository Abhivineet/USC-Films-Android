package com.abhivineet.uscfilms.models;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewCard implements Serializable {
    private final String author;
    private final String content;
    private final String createdAt;
    private final String rating;

    private String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    private String getDateFormattedString() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat requiredFormat = new SimpleDateFormat("E, MMM dd yyyy");
        try {
            Date date = simpleDateFormat.parse(getCreatedAt());
            return requiredFormat.format(date);

        } catch (ParseException e) {
            return "";
        }

    }

    public String getRating() {
        return rating;
    }

    public String getReviewCardHeader(){
        return "by " + getAuthor() + " on " + getDateFormattedString();
    }

    public ReviewCard(String author, String content, String createdAt, String rating) {
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
        this.rating = rating;
    }

    @NotNull
    public String toString(){
        return getAuthor() + " " + getContent() + " " + getCreatedAt() + " " + getRating();
    }
}
