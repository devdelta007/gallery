package com.gallery.picture.foto.event;

public class ImageDeleteEvent {

    String deletePath;
    public ImageDeleteEvent(String deletePath) {
        this.deletePath = deletePath;
    }

    public String getDeletePath() {
        return deletePath;
    }

    public void setDeletePath(String deletePath) {
        this.deletePath = deletePath;
    }
}
