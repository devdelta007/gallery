package com.gallery.picture.foto.Model;

import androidx.annotation.NonNull;

import com.gallery.picture.foto.Model.ImageDetail;
import com.gallery.picture.foto.service.ImageDataService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PhotoData {

    public String BucketId;
    public String Id;
    public ArrayList<ImageDetail> PathList;
    public int Type;
    String filePath;
    String fileSize;
    public String folderName = "";
    String thumbnails;
    String duration;
    long size;
    String date;
    boolean isSelected;
    boolean isCheckboxVisible = false;

    boolean isFavorite = false;

    long dateValue;
    public String folderPath;

    String fileName;
    List<String> locationData;

    public List<String> getLocationData() {
        return locationData;
    }

    public void setLocationData(List<String> locationData) {

        this.locationData = locationData;
    }
    public PhotoData(){
        
    }

    public PhotoData(String folder, String id, String bucketId, String date, String path, String title, long size, long d, boolean b) {
        this.folderName = folder;
        this.Id = id;
        this.BucketId = bucketId;
        this.date = date;
        this.filePath = path;
        this.fileName = title;
        this.size = size;
        this.dateValue = d;
        this.BucketId = bucketId;

        this.isFavorite = b;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public ArrayList<ImageDetail> getPathList() {
        return PathList;
    }

    public void setPathList(ArrayList<ImageDetail> pathList) {
        PathList = pathList;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }


    @Override
    public String toString() {
        return "PhotoData{" +
                "BucketId='" + BucketId + '\'' +
                ", Id='" + Id + '\'' +
                ", PathList=" + PathList +
                ", Type=" + Type +
                ", filePath='" + filePath + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", folderName='" + folderName + '\'' +
                ", thumbnails='" + thumbnails + '\'' +
                ", duration='" + duration + '\'' +
                ", size=" + size +
                ", date='" + date + '\'' +
                ", isSelected=" + isSelected +
                ", isCheckboxVisible=" + isCheckboxVisible +
                ", isFavorite=" + isFavorite +
                ", dateValue=" + dateValue +
                ", folderPath='" + folderPath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", locationData=" + locationData +
                '}';
    }

    public String getBucketId() {
        return BucketId;
    }

    public void setBucketId(String bucketId) {
        BucketId = bucketId;
    }



    public long getDateValue() {
        return dateValue;
    }

    public void setDateValue(long dateValue) {
        this.dateValue = dateValue;
    }

    //    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isCheckboxVisible() {
        return isCheckboxVisible;
    }

    public void setCheckboxVisible(boolean checkboxVisible) {
        isCheckboxVisible = checkboxVisible;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
