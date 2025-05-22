package com.example.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.R;
import com.example.app.activities.BookDetailActivity;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.models.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context context;
    private List<Book> bookList;
    private Map<Integer, String> categoryMap; // Map để tra cứu categoryName nhanh

    public BookAdapter(Context context, List<Book> bookList, List<Category> categoryList) {
        this.context = context;
        this.bookList = bookList != null ? bookList : new ArrayList<>();
        // Chuyển categoryList thành Map để tra cứu nhanh
        this.categoryMap = new HashMap<>();
        updateCategoryMap(categoryList);
    }

    private void updateCategoryMap(List<Category> categoryList) {
        categoryMap.clear();
        if (categoryList != null) {
            for (Category category : categoryList) {
                categoryMap.put(category.getId(), category.getName());
            }
        }
    }

    public void setBooks(List<Book> books) {
        this.bookList = books != null ? books : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCategories(List<Category> categories) {
        updateCategoryMap(categories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.textTitle.setText(book.getName() != null ? book.getName() : "Unknown Title");

        // Xử lý giá
        holder.textPrice.setText(book.getPrice() > 0 ? String.format("%,.0f đ", book.getPrice()) : "N/A");

        // Load hình ảnh bằng Glide
        List<Image> images = book.getImages();
        if (images != null && !images.isEmpty() && images.get(0).getUrl() != null) {
            Glide.with(context)
                    .load(images.get(0).getUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.placeholder_book)
                    .into(holder.imageBook);
        } else {
            holder.imageBook.setImageResource(R.drawable.placeholder_book);
        }

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("bookId", book.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBook;
        TextView textTitle, textPrice;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBook = itemView.findViewById(R.id.imageBook);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPrice = itemView.findViewById(R.id.textPrice);
        }
    }

    public void updateList(List<Book> newBookList) {
        bookList.clear();
        bookList.addAll(newBookList != null ? newBookList : new ArrayList<>());
        notifyDataSetChanged();
    }
}