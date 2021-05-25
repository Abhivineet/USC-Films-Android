package com.abhivineet.uscfilms.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class MediaCard implements Serializable {
    private final String media;
    private final String title;
    private final String imagePath;
    private final String id;

    public String getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getId() {
        return id;
    }

    public MediaCard(String media, String title, String imagePath, String id) {
        this.media = media;
        this.title = title;
        this.imagePath = imagePath;
        this.id = id;
    }

    @NotNull
    public String toString(){
        return getMedia() + " " + getTitle() + " " + getImagePath() + " " + getId();
    }
}
