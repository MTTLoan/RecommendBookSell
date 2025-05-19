package com.example.app.adapters;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context context;
    private List<Book> bookList;
    private List<Category> categoryList;

    public BookAdapter(Context context, List<Book> bookList, List<Category> categoryList) {
        this.context = context;
        this.bookList = bookList;
        this.categoryList = categoryList;
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
        // Find category name by categoryId
        String categoryName = "Unknown Category";
        for (Category category : categoryList) {
            if (category.getId() == book.getCategoryId()) {
                categoryName = category.getName();
                break;
            }
        }
        holder.textCategory.setText(categoryName);
        holder.textTitle.setText(book.getName());
//        holder.textAuthor.setText(book.getAuthor());
        holder.textPrice.setText(String.format("%,.0f đ", book.getPrice()));

        // Load image using Glide
        if (!book.getImages().isEmpty()) {
            Glide.with(context)
                    .load(book.getImages().get(0).getUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.placeholder_book)
                    .into(holder.imageBook);
        } else {
            holder.imageBook.setImageResource(R.drawable.placeholder_book);
        }

        // Set click listener to navigate to BookDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("book", bookList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBook;
        TextView textCategory, textTitle, textAuthor, textPrice;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBook = itemView.findViewById(R.id.imageBook);
            textCategory = itemView.findViewById(R.id.textCategory);
            textTitle = itemView.findViewById(R.id.textTitle);
            textAuthor = itemView.findViewById(R.id.textAuthor);
            textPrice = itemView.findViewById(R.id.textPrice);
        }
    }

    public void updateList(List<Book> newBookList) {
        bookList.clear();
        bookList.addAll(newBookList);
        notifyDataSetChanged();
    }
}