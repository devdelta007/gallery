package com.gallery.picture.foto.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gallery.picture.foto.room.Location;
import com.gallery.picture.foto.room.LocationDAO;

@Database(entities = {Location.class}, version = 3)
public abstract class AppDataBase extends RoomDatabase {
    public abstract LocationDAO locationDao();
}