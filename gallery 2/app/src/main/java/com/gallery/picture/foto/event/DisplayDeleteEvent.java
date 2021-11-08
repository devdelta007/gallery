package com.gallery.picture.foto.event;

import java.util.ArrayList;

public class DisplayDeleteEvent {
    ArrayList<String> deleteList = new ArrayList<>();

    public DisplayDeleteEvent(ArrayList<String> deleteList) {
        this.deleteList = deleteList;
    }

    public ArrayList<String> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(ArrayList<String> deleteList) {
        this.deleteList = deleteList;
    }
}
