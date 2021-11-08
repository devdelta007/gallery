package com.gallery.picture.foto.Model;

public class ImageDetail {
    String Name;
    String DateModified;
    String DisplayName;
    String imageId;
    boolean isFavorite = false;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }


    public ImageDetail(String name, String dateModified, String DisplayName, boolean isFavorite) {
        Name = name;
        DateModified = dateModified;
        this.DisplayName =  DisplayName;
        this.isFavorite = isFavorite;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDateModified() {
        return DateModified;
    }

    public void setDateModified(String dateModified) {
        DateModified = dateModified;
    }

    @Override
    public String toString() {
        return "ImageDetail{" +
                "Name='" + Name + '\'' +
                ", DateModified='" + DateModified + '\'' +
                ", DisplayName='" + DisplayName + '\'' +
                '}';
    }
}
