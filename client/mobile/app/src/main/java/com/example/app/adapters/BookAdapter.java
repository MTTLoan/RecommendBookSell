package com.example.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.R;
import com.example.app.activities.BookDetailActivity;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.models.Image;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Book> bookList;
    private Map<Integer, String> categoryMap;

    // View types
    private static final int VIEW_TYPE_REGULAR = 0;
    private static final int VIEW_TYPE_BEST_SELLER = 1;
    private int viewType;

    public BookAdapter(Context context, List<Book> bookList, List<Category> categoryList, int viewType) {
        this.context = context;
        this.bookList = bookList != null ? bookList : new ArrayList<>();
        this.viewType = viewType;
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

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BEST_SELLER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_book_best_sale, parent, false);
            return new BestSellerViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
            return new BookViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Book book = bookList.get(position);

        if (holder instanceof BestSellerViewHolder) {
            BestSellerViewHolder bestSellerHolder = (BestSellerViewHolder) holder;
            bestSellerHolder.textTitle.setText(book.getName() != null ? book.getName() : "Unknown Title");
            bestSellerHolder.textPrice.setText(book.getPrice() > 0 ? String.format("%,.0f đ", book.getPrice()) : "N/A");
            bestSellerHolder.txtSold.setText(String.format("Đã bán: %d", book.getTotalQuantitySold()));
            bestSellerHolder.textSale.setText("#" + (position + 1));

            Image image = book.getFirstImage();
            if (image != null && image.getUrl() != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.placeholder_book)
                        .into(bestSellerHolder.imageBook);
            } else {
                bestSellerHolder.imageBook.setImageResource(R.drawable.placeholder_book);
            }
        } else {
            BookViewHolder bookHolder = (BookViewHolder) holder;
            bookHolder.textTitle.setText(book.getName() != null ? book.getName() : "Unknown Title");
            bookHolder.textPrice.setText(book.getPrice() > 0 ? String.format("%,.0f đ", book.getPrice()) : "N/A");

            Image image = book.getFirstImage();
            if (image != null && image.getUrl() != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.placeholder_book)
                        .into(bookHolder.imageBook);
            } else {
                bookHolder.imageBook.setImageResource(R.drawable.placeholder_book);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (book.getId() <= 0) {
                Toast.makeText(context, "ID sách không hợp lệ.", Toast.LENGTH_SHORT).show();
                return;
            }
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

    public static class BestSellerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBook;
        TextView textTitle, textPrice, textSale, txtSold;

        public BestSellerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBook = itemView.findViewById(R.id.imageBook);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPrice = itemView.findViewById(R.id.textPrice);
            txtSold = itemView.findViewById(R.id.totalSold);
            textSale = itemView.findViewById(R.id.textSale);
        }
    }

    public void updateList(List<Book> newBookList) {
        bookList.clear();
        bookList.addAll(newBookList != null ? newBookList : new ArrayList<>());
        notifyDataSetChanged();
    }

}