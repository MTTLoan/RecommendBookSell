package com.example.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.models.Book;
import com.example.app.models.Image;

import java.util.List;

public class BookImageAdapter extends RecyclerView.Adapter<BookImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Image> images;
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public BookImageAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = images.get(position);
        Glide.with(context)
                .load(image.getUrl())
                .error(R.drawable.placeholder_book) // ảnh mặc định khi lỗi
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            if (onImageClickListener != null) {
                onImageClickListener.onImageClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.onImageClickListener = listener;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSliderImage); // imageView trong layout item_book_image.xml
        }
    }
}
