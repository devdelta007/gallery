package com.gallery.picture.foto.event;

import java.util.ArrayList;

public class DisplayFavoriteEvent {
    ArrayList<String> favList = new ArrayList<>();

    boolean isFavorite = false;
    String filePath;

    public DisplayFavoriteEvent(ArrayList<String> favList, boolean isFavorite) {
        this.favList = favList;
        this.isFavorite = isFavorite;
    }

    public DisplayFavoriteEvent(String filePath, boolean isFavorite) {
        this.filePath = filePath;
        this.isFavorite = isFavorite;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<String> getFavList() {
        return favList;
    }

    public void setFavList(ArrayList<String> favList) {
        this.favList = favList;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
