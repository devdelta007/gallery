package com.gallery.picture.foto.room;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

public class ListConvertor {

    @TypeConverter
    public static List<String> toLS(String timestamp) {
        List<String> a = new ArrayList<>();
        a.add(timestamp);
        return (timestamp == null || timestamp.isEmpty()) ? null : a;
    }

    @TypeConverter
    public static String toStr(List<String> date) {
        return (date == null || date.isEmpty())? null : date.get(0);
    }


}
