package com.gallery.picture.foto.event;

import java.io.File;
import java.util.ArrayList;

public class CopyMoveEvent {
    //move & copy file list
    ArrayList<File> copyMoveList = new ArrayList<>();

    // delete file list
    ArrayList<String> deleteList = new ArrayList<>();

    // copy = 1; move  = 2
    int type = 0;

    public CopyMoveEvent(ArrayList<File> copyMoveList, int type, ArrayList<String> deleteList) {
        this.deleteList = deleteList;
        this.copyMoveList = copyMoveList;
        this.type = type;
    }


    public CopyMoveEvent(String compressFilePath) {
        ArrayList<File> copyMoveList = new ArrayList<>();

        File file = new File(compressFilePath);
        copyMoveList.add(file);

        this.deleteList = new ArrayList<>();
        this.copyMoveList = copyMoveList;
        this.type = 3;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<File> getCopyMoveList() {
        return copyMoveList;
    }

    public void setCopyMoveList(ArrayList<File> copyMoveList) {
        this.copyMoveList = copyMoveList;
    }

    public ArrayList<String> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(ArrayList<String> deleteList) {
        this.deleteList = deleteList;
    }
}
