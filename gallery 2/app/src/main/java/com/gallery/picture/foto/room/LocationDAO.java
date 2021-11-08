package com.gallery.picture.foto.room;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gallery.picture.foto.room.Location;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface LocationDAO {
    @Query("SELECT * FROM location")
    List<com.gallery.picture.foto.room.Location> getAll();

    @Query("SELECT * FROM location WHERE uid IN (:userIds)")
    List<com.gallery.picture.foto.room.Location> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM location WHERE path LIKE :first" +
            " LIMIT 1")
    com.gallery.picture.foto.room.Location findByPath(String first);

    @Insert
    void insertAll(com.gallery.picture.foto.room.Location... locations);

    @Insert
    void insert(com.gallery.picture.foto.room.Location location);

    @Delete
    void delete(Location location);

    @Query("DELETE FROM location WHERE path=:Path")
    void deleteLocation(String Path);

    @Query("DELETE FROM location")
    void deleteAll();
}