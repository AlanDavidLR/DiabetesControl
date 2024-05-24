package com.example.diabetescontrol;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {

    private Context context;
    private List<ImageSlide> imageSlides;

    public ImageSliderAdapter(Context context, List<ImageSlide> imageSlides) {
        this.context = context;
        this.imageSlides = imageSlides;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slide_item, parent, false);
        return new ImageSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        ImageSlide imageSlide = imageSlides.get(position);
        holder.imageView.setImageResource(imageSlide.getImageResId());
        holder.descriptionTextView.setText(imageSlide.getDescription());

        holder.imageView.setOnClickListener(v -> {
            // Abrir la URL en el navegador
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageSlide.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageSlides.size();
    }

    public static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionTextView;

        public ImageSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
