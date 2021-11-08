package com.gallery.picture.foto.utils;

import com.gallery.picture.foto.Model.PhotoData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Constant {

    public static List<PhotoData> displayImageList = new ArrayList<>();
    public static List<PhotoData> displayVideoList = new ArrayList<>();

    public static ArrayList<File> pastList = new ArrayList<>();
    public static boolean isCopyData = false;
    public static boolean isFileFromSdCard = false;
    public static String storagePath ;
    public static ArrayList<String> arrayListFilePaths = new ArrayList<>();

    public final static String SHARED_PREFS = "my_gallery";
    public final static String SHARED_PREFS_RATE_US = "my_gallery_rate_us";
    public final static String SHARED_PREFS_DIR_LIST_GRID = "my_gallery_dir_list_grid";
    public final static String SHARED_PREFS_DOCUMENT_LIST_GRID = "my_gallery_document_list_grid";
    public final static String SHARED_PREFS_SHOW_HIDDEN_FILE = "my_gallery_hidden_file";
    public final static String SHARED_PREFS_SORT_FILE_LIST = "my_gallery_sort_file_list";
    public final static String SHARED_PREFS_FAVOURITE_LIST = "my_gallery_favourite_list";
    public final static String PREF_SDCARD_TREE_URI = "my_gallery_sd_card_tree_uri";

    public static final int STICKER_BTN_HALF_SIZE = 30;

    public static boolean isOpenImage = false;
    public static boolean isShowFirstTimeADs = false;
    public static boolean isShowFirstTimeADsOnPhotos = false;
    public static boolean isShowFirstTimeADsOnAlbums = false;
    public static boolean isShowFirstTimeOptionADs = false;
    public static boolean isShowFirstTimeADsOnLocation = false;
}
