package com.gallery.picture.foto.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gallery.picture.foto.room.ListConvertor;

import java.util.List;

@Entity(tableName = "location")
public class Location {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "path")
    public String path;

    @ColumnInfo(name = "location")
    @TypeConverters(ListConvertor.class)
    public List<String> location;

    public Location(String path, List<String> location) {
        this.path = path;
        this.location = location;
    }

    public Location() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }
}