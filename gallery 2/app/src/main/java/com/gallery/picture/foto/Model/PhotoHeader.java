package com.gallery.picture.foto.Model;

import com.gallery.picture.foto.Model.PhotoData;

import java.util.ArrayList;

public class PhotoHeader {
    String title,folderPath;

    ArrayList<com.gallery.picture.foto.Model.PhotoData> photoList = new ArrayList<>();

    boolean isSelect = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<com.gallery.picture.foto.Model.PhotoData> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<PhotoData> photoList) {
        this.photoList = photoList;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
