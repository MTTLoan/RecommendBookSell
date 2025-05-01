package com.example.app.adapters;

import android.content.Context;
import android.util.Log;
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

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Book> bookList;

    public ReviewAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
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

        // Hiển thị thông tin sách
        holder.tvBookName.setText(book.getName());

        // Để trống RatingBar và các EditText để người dùng nhập
        holder.ratingBar.setRating(0); // Mặc định là 0 sao
        holder.etContent.setText(""); // Để trống đánh giá nội dung
        holder.etPackaging.setText(""); // Để trống đánh giá bao bì
        holder.etShipping.setText(""); // Để trống đánh giá đóng gói
        holder.etComment.setText(""); // Để trống đánh giá riêng

        // Thêm sự kiện khi RatingBar thay đổi
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) { // Chỉ xử lý khi người dùng thay đổi (không phải do setRating())
                    String bookName = book.getName();
                    Log.d("ReviewAdapter", "Book: " + bookName + ", Rating changed to: " + rating);
                    // TODO: Có thể lưu rating vào một danh sách tạm thời hoặc gửi lên server
                }
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
        public RatingBar ratingBar;
        public EditText etContent;
        public EditText etPackaging;
        public EditText etShipping;
        public EditText etComment;

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
}