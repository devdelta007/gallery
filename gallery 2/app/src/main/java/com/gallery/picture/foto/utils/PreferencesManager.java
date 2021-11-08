package com.gallery.picture.foto.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.gallery.picture.foto.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {
    public static void saveToDirList_Grid(Context context, boolean value) {

        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_DIR_LIST_GRID, value);
        editor.commit();

    }

    public static boolean getDirList_Grid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_DIR_LIST_GRID, false);
    }

    public static void saveToDocList_Grid(Context context, boolean value) {

        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_DOCUMENT_LIST_GRID, value);
        editor.commit();

    }

    public static boolean getDocList_Grid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_DOCUMENT_LIST_GRID, false);
    }


    public static void saveToShowHidden(Context context, boolean value) {

        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_SHOW_HIDDEN_FILE, value);
        editor.commit();

    }

    public static boolean getShowHidden(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_SHOW_HIDDEN_FILE, false);
    }


    public static void saveToSortType(Context context, int value) {
        // 1- name_ascending, 2- name_descending, 3 -Size_Descending ,4 -Size_Ascending, 5 - Time_Ascending ,6- Time_Descending
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_SORT_FILE_LIST, value);
        editor.commit();
    }

    public static int getSortType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getInt(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_SORT_FILE_LIST, 1);
    }


    public static void setRateUs(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_RATE_US, value);
        editor.commit();

    }

    public static boolean getRate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_RATE_US, false);
    }

    public static String getSDCardTreeUri(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        String treeUri = preferences.getString(com.gallery.picture.foto.utils.Constant.PREF_SDCARD_TREE_URI, "");
        return treeUri;
    }

    public static void setSDCardTreeUri(Context context, String treeUri) {
        SharedPreferences preferences = context.getSharedPreferences(
                com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(com.gallery.picture.foto.utils.Constant.PREF_SDCARD_TREE_URI, treeUri);
        editor.apply();
    }

    public static ArrayList<String> getFavouriteList(Context context) {
        ArrayList<String> favouriteList = new ArrayList<>();


        try {
            SharedPreferences preferences = context.getSharedPreferences(
                    com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);

            String response = preferences.getString(com.gallery.picture.foto.utils.Constant.SHARED_PREFS_FAVOURITE_LIST, "");

            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();
            favouriteList = gson.fromJson(response, type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return favouriteList;
    }

    public static void setFavouriteList(Context context, ArrayList<String> favouriteList) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(
                    com.gallery.picture.foto.utils.Constant.SHARED_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(favouriteList);

            editor.putString(Constant.SHARED_PREFS_FAVOURITE_LIST, json);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}