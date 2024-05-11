package com.example.diabetescontrol;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SlideViewHolder> {

    private List<ImageSlide> imageSlides;
    private Context context;

    public ImageSliderAdapter(Context context, List<ImageSlide> imageSlides) {
        this.context = context;
        this.imageSlides = imageSlides;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        ImageSlide slide = imageSlides.get(position);
        holder.imageView.setImageResource(slide.getImageResId());
        holder.descriptionTextView.setText(slide.getDescription());
    }

    @Override
    public int getItemCount() {
        return imageSlides.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionTextView;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
