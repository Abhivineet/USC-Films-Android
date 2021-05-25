package com.abhivineet.uscfilms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.abhivineet.uscfilms.models.ReviewCard;

import java.util.Objects;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Objects.requireNonNull(getSupportActionBar()).hide();
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView author = (TextView) findViewById(R.id.author_header);
        TextView content = (TextView) findViewById(R.id.content);
        Intent intent = getIntent();
        ReviewCard card = (ReviewCard) intent.getSerializableExtra("Review");
        rating.setText(card.getRating());
        content.setText(card.getContent());
        author.setText(card.getReviewCardHeader());
    }
}