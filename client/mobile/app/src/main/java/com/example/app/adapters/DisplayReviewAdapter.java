package com.example.app.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.models.Review;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DisplayReviewAdapter extends RecyclerView.Adapter<DisplayReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public DisplayReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    // Method to update reviews
    public void setReviews(List<Review> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
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
        // Use username if available, fallback to userId
        holder.tvUsername.setText(review.getUsername() != null ? review.getUsername() : "Người dùng #" + review.getUserId());

        // Parse and format createdAt from ISO 8601 string
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(review.getCreatedAt());
            holder.tvCreatedAt.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvCreatedAt.setText(review.getCreatedAt()); // Fallback to raw string
            Log.e("DisplayReviewAdapter", "Error parsing date: " + review.getCreatedAt(), e);
        }

        // Set rating for RatingBar
        holder.ratingBar.setRating(review.getRating());

        // Set review comment
        holder.tvReviewDescription.setText(review.getComment() != null ? review.getComment() : "Không có bình luận");
    }

    @Override
    public int getItemCount() {
        return reviewList != null ? reviewList.size() : 0;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvCreatedAt, tvReviewDescription;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ratingBar = itemView.findViewById(R.id.rbrating_bar);
            tvReviewDescription = itemView.findViewById(R.id.tvReviewDescription);
        }
    }
}