package com.example.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.models.Review;

import java.util.List;

public class DisplayReviewAdapter extends RecyclerView.Adapter<DisplayReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public DisplayReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvUsername.setText("Người dùng #" + review.getUserId());
        holder.tvCreatedAt.setText(review.getCreatedAt());
        holder.tvRatingLabel.setText(String.format("%.1f/5", review.getRating()));
        holder.tvReviewDescription.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviewList != null ? reviewList.size() : 0;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvCreatedAt, tvRatingLabel, tvReviewDescription;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvRatingLabel = itemView.findViewById(R.id.tvRatingLabel);
            tvReviewDescription = itemView.findViewById(R.id.tvReviewDescription);
        }
    }
}
