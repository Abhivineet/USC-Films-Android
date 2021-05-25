package com.abhivineet.uscfilms.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhivineet.uscfilms.R;
import com.abhivineet.uscfilms.ReviewActivity;
import com.abhivineet.uscfilms.models.ReviewCard;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewCardRecyclerViewAdapter extends RecyclerView.Adapter {
    private final ArrayList<ReviewCard> cardList;

    public ReviewCardRecyclerViewAdapter(ArrayList<ReviewCard> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_review_card, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ReviewCard reviewCardItem = cardList.get(position);
        ReviewHolder reviewCardView = (ReviewHolder) holder;

        reviewCardView.getReviewCard().setOnClickListener(view -> {
            Intent reviewIntent = new Intent(reviewCardView.context, ReviewActivity.class);
            reviewIntent.putExtra("Review", reviewCardItem);
            reviewCardView.context.startActivity(reviewIntent);
        });

        reviewCardView.getReviewContent().setText(reviewCardItem.getContent());
        reviewCardView.getRatingContent().setText(reviewCardItem.getRating());
        reviewCardView.getAuthorContent().setText(reviewCardItem.getReviewCardHeader());

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class ReviewHolder extends RecyclerView.ViewHolder{
        private final CardView reviewCard;
        private final TextView authorContent;
        private final TextView ratingContent;
        private final TextView reviewContent;
        private final Context context;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            reviewCard = itemView.findViewById(R.id.review_card);
            authorContent = itemView.findViewById(R.id.review_author_header);
            ratingContent = itemView.findViewById(R.id.rating_text);
            reviewContent = itemView.findViewById(R.id.review_text);
        }

        public TextView getAuthorContent() {
            return authorContent;
        }

        public TextView getRatingContent() {
            return ratingContent;
        }

        public TextView getReviewContent() {
            return reviewContent;
        }

        public CardView getReviewCard() {
            return reviewCard;
        }
    }
}
