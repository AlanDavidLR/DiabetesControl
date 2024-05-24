package com.example.diabetescontrol;

public class ImageSlide {
    private int imageResId;
    private String description;
    private String url;

    public ImageSlide(int imageResId, String description, String url) {
        this.imageResId = imageResId;
        this.description = description;
        this.url = url;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}

