package com.gallery.picture.foto.Model;

import java.util.ArrayList;

public class GalleryImages {

    String displayName;
    String data;
    String date;
    String dateModified;
    String bucket;
    static public ArrayList<GalleryImages> galleryImagesArray = new ArrayList<GalleryImages>();


    public GalleryImages(String displayName, String data, String date, String dateModified, String bucket) {
        this.displayName = displayName;
        this.data = data;
        this.date = date;
        this.dateModified = dateModified;
        this.bucket = bucket;


    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public static ArrayList<GalleryImages> getGalleryImagesArray() {
        return galleryImagesArray;
    }

    public static void setGalleryImagesArray(ArrayList<GalleryImages> galleryImagesArray) {
        GalleryImages.galleryImagesArray = galleryImagesArray;
    }

    @Override
    public String toString() {
        return "GalleryImages{" +
                "displayName='" + displayName + '\'' +
                ", data='" + data + '\'' +
                ", date='" + date + '\'' +
                ", dateModified='" + dateModified + '\'' +
                ", bucket='" + bucket + '\'' +
                '}';
    }
}
