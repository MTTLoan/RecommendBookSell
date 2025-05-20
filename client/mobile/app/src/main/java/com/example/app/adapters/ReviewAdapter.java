package com.example.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.models.Book;
import com.example.app.models.Image;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Book> bookList;
    private List<Float> ratings; // Lưu rating cho mỗi sách

    public ReviewAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.ratings = new ArrayList<>();
        // Khởi tạo ratings với giá trị mặc định
        for (int i = 0; i < bookList.size(); i++) {
            ratings.add(0.0f); // Mặc định 0, yêu cầu người dùng chọn
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Book book = bookList.get(position);

        // Cập nhật UI
        holder.tvBookName.setText(book.getName());
        // Lấy hình ảnh đầu tiên
        if (book.getImages() != null && !book.getImages().isEmpty()) {
            Image firstImage = book.getImages().get(0);
            if (firstImage.getUrl() != null && !firstImage.getUrl().isEmpty()) {
                Picasso.get().load(firstImage.getUrl()).into(holder.ivBookImage);
            } else {
                holder.ivBookImage.setImageResource(R.drawable.placeholder_book);
            }
        } else {
            holder.ivBookImage.setImageResource(R.drawable.placeholder_book);
        }

        // Cập nhật RatingBar
        holder.ratingBar.setRating(ratings.get(position));
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                ratings.set(position, rating);
                android.util.Log.d("ReviewAdapter", "Rating changed for book " + book.getName() + ": " + rating);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookImage;
        TextView tvBookName;
        RatingBar ratingBar;
        public EditText etContent, etPackaging, etShipping, etComment;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookImage = itemView.findViewById(R.id.iv_book_image);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            etContent = itemView.findViewById(R.id.et_content);
            etPackaging = itemView.findViewById(R.id.et_packaging);
            etShipping = itemView.findViewById(R.id.et_shipping);
            etComment = itemView.findViewById(R.id.et_comment);
        }
    }

    // Getter để ReviewActivity lấy ratings
    public List<Float> getRatings() {
        return ratings;
    }
}