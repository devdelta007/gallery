package com.gallery.picture.foto.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.gallery.picture.foto.R;
import com.gallery.picture.foto.utils.PreferencesManager;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.pm.PackageManager.GET_UNINSTALLED_PACKAGES;
import static android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES;
//import static com.devicecare.battery.saver.Constants.DAY_MILLIS;
//import static com.devicecare.battery.saver.Constants.HOUR_MILLIS;
//import static com.devicecare.battery.saver.Constants.MINUTE_MILLIS;
//import static com.devicecare.battery.saver.Constants.REQUEST_SDCARD_WRITE_PERMISSION;

public class StorageUtils {

    public final static int REQUEST_SDCARD_WRITE_PERMISSION = 300;

    public static boolean hasOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean hasLollipopMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public static boolean openable(PackageManager packageManager, String packageName) {
        return packageManager.getLaunchIntentForPackage(packageName) != null;
    }

   /* public static Drawable parsePackageIcon(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            return manager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return context.getResources().getDrawable(R.drawable.ic_app_icon);
    }*/

    /*public static Drawable getAppIcon(Context context, String apkPath) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath, 0);
            packageInfo.applicationInfo.sourceDir = apkPath;
            packageInfo.applicationInfo.publicSourceDir = apkPath;

            Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
            return icon;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getResources().getDrawable(R.drawable.ic_app_icon);
    }*/

    public static String isMyLauncherDefault(Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;

        return str;
    }

    public static boolean checkWriteSettings(Context context) {
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        return permission;
    }

    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessInfo(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = new ArrayList<>();
        String prevProcess = "";
        if (hasOreo()) {
            PackageManager packageManager = ctx.getPackageManager();
            List<PackageInfo> allAppList = packageManager.getInstalledPackages(getAppListFlag());
            for (PackageInfo packageInfo : allAppList) {
                ActivityManager.RunningAppProcessInfo info = new ActivityManager.RunningAppProcessInfo(
                        packageInfo.packageName, packageInfo.applicationInfo.uid, null
                );
                info.uid = packageInfo.applicationInfo.uid;
                info.importance = ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
                appProcessInfos.add(info);
            }
            return appProcessInfos;
        } else if (hasNougat()) {
            List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(1000);
            for (ActivityManager.RunningServiceInfo process : runningServices) {
                ActivityManager.RunningAppProcessInfo info = new ActivityManager.RunningAppProcessInfo(
                        process.process, process.pid, null
                );
                info.uid = process.uid;
                info.importance = process.foreground ? ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND : ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;

                if (!prevProcess.equals(process.process)) {
                    prevProcess = process.process;
                    appProcessInfos.add(info);
                }
            }
            return appProcessInfos;
        } else if (hasLollipopMR1()) {
//            List<AndroidAppProcess> runningAppProcesses = AndroidProcesses.getRunningAppProcesses();
//            for (AndroidAppProcess process : runningAppProcesses) {
//                ActivityManager.RunningAppProcessInfo info = new ActivityManager.RunningAppProcessInfo(
//                        process.name, process.pid, null
//                );
//                info.uid = process.uid;
//                info.importance = process.foreground ? ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND : ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
//                // TODO: Get more information about the process. pkgList, importance, lru, etc.
//                appProcessInfos.add(info);
//            }
            return appProcessInfos;
        }
        return am.getRunningAppProcesses();
    }

    private static int getAppListFlag() {
        return hasNougat() ? MATCH_UNINSTALLED_PACKAGES : GET_UNINSTALLED_PACKAGES;
    }

    public static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;
        if (size < Kb) return floatForm(size) + " B";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " KB";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " MB";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " GB";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " TB";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " PB";
        if (size >= Eb) return floatForm((double) size / Eb) + " Eb";
        return "???";
    }

    public static String getSize(long size) {
        long n = 1000;
        String s = "";
        double kb = size / n;
        double mb = kb / n;
        double gb = mb / n;
        double tb = gb / n;
        if (size < n) {
            s = size + " B";
        } else if (size >= n && size < (n * n)) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size >= (n * n) && size < (n * n * n)) {
            s = String.format("%.2f", mb) + " MB";
        } else if (size >= (n * n * n) && size < (n * n * n * n)) {
            s = String.format("%.2f", gb) + " GB";
        } else if (size >= (n * n * n * n)) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    public static String StoragePath(String StorageType, Context context) {
        List<String> paths = getStorageDirectories(context);
        if (paths.size() > 0) {
            try {
                if (StorageType.equalsIgnoreCase("InternalStorage")) {
                    return paths.get(0);

                } else if (StorageType.equalsIgnoreCase("ExternalStorage")) {
                    if (paths.size() >= 1)
                        return paths.get(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static boolean externalMemoryAvailable(Activity context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null) {
            Log.e("Utils", "storages: " + storages.toString());
            return true;
        } else
            return false;
    }


    public static List<String> getStorageDirectories(Context context) {

        final Pattern DIR_SEPARATOR = Pattern.compile("/");
        // Final set of paths
        final ArrayList<String> rv = new ArrayList<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPARATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            rv.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String strings[] = getExtSdCardPaths(context);
            for (String s : strings) {
                File f = new File(s);
                if (!rv.contains(s) && canListFiles(f))
                    rv.add(s);
            }
        }
        return rv;
    }

    public static boolean canListFiles(File f) {
        try {
            if (f.canRead() && f.isDirectory())
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String[] getExtSdCardPaths(Context context) {
        List<String> paths = new ArrayList<String>();
        for (File file : ContextCompat.getExternalFilesDirs(context, "external")) {
            if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("FileUtils", "Unexpected external manager dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        if (paths.isEmpty()) paths.add("/storage/sdcard1");
        return paths.toArray(new String[0]);
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

 /*   public static String getDateWithTime(long time) {
        DateFormat inputFormat = new SimpleDateFormat("dd MMM");
        DateFormat inputFormatWithYear = new SimpleDateFormat("dd MMM yyyy");

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int year = cal.get(Calendar.YEAR);

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 5 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else if (year == currentYear) {
            return inputFormat.format(new Date(time));
        } else {
            return inputFormatWithYear.format(new Date(time));
        }
    }*/

    public static String formateSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getFileExtension(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf("."));
        return extension.replace(".", "");
    }

    public static final long USAGE_TIME_MIX = 500;
    public static final long A_DAY = 86400 * 1000;

  /*  public static long[] getTimeRange(SortOrder sort) {
        long[] range;
        switch (sort) {
            case TODAY:
                range = getTodayRange();
                break;
            case THIS_WEEK:
                range = getThisWeek();
                break;
            case MONTH:
                range = getThisMonth();
                break;
            default:
                range = getTodayRange();
        }
        return range;
    }*/

    private static long[] getThisMonth() {
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new long[]{cal.getTimeInMillis(), timeNow};
    }

    public static long[] getTodayRange() {
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new long[]{cal.getTimeInMillis(), timeNow};
    }

    private static long[] getThisWeek() {
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = timeNow - A_DAY * 6;
        return new long[]{start, timeNow};
    }

    public static boolean isSystemApp(PackageManager manager, String packageName) {

        boolean isSystemApp = false;
        try {
            ApplicationInfo applicationInfo = manager.getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                        || (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return isSystemApp;
    }

    public static boolean isInstalled(PackageManager packageManager, String packageName) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationInfo != null;
    }

    public static String parsePackageName(PackageManager pckManager, String data) {
        ApplicationInfo applicationInformation;
        try {
            applicationInformation = pckManager.getApplicationInfo(data, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInformation = null;
        }
        return (String) (applicationInformation != null ? pckManager.getApplicationLabel(applicationInformation) : data);
    }

    public static String humanReadableMillis(long milliSeconds) {
        long second = milliSeconds / 1000L;
        if (second < 60) {
            return String.format("%s sec", second);
        } else if (second < 60 * 60) {
            return String.format("%s min %s sec", second / 60, second % 60);
        } else {
            return String.format("%s hrs %s min %s sec", second / 3600, second % (3600) / 60, second % (3600) % 60);
        }
    }

    public static List<Long> getXAxisValue(long time) {
        List<Long> dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 7; i++) {
            if (i != 0) {
                calendar.add(Calendar.DATE, -1);
            }
            dayList.add(calendar.getTimeInMillis());
        }

        Collections.reverse(dayList);

        return dayList;
    }

    public static String getDay(long millisecond) {
        String date = "";
        date = android.text.format.DateFormat.format("EEE", new Date(millisecond)).toString();
        return date;
    }

    public static String humanReadableMillis1(long milliSeconds) {
        long second = milliSeconds / 1000L;
        if (second < 60) {
            return String.format("%ss", second);
        } else if (second < 60 * 60) {
            return String.format("%sm", second / 60);
        } else {
            return String.format("%sh", second / 3600);
        }
    }

    public static long[] getDayRangeChartTime(long time, boolean isCurrent) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.setTimeInMillis(time);
        if (!isCurrent) {
            cal.add(Calendar.DATE, 1);
        }
        long end = cal.getTimeInMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeInMillis(time);

        long start = calendar.getTimeInMillis();

        return new long[]{start, end};
    }

    public static long[] getDayRangeTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.setTimeInMillis(time);
        long end = cal.getTimeInMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start = calendar.getTimeInMillis();

        return new long[]{start, end};
    }

    public static boolean isImage(String filename) {
        String type = getFileType(filename);
        if (type != null
                && (type.equals("jpg") || type.equals("gif")
                || type.equals("png") || type.equals("jpeg")
                || type.equals("bmp") || type.equals("wbmp")
                || type.equals("ico") || type.equals("jpe"))) {
            return true;
        }
        return false;
    }

    public static String getFileType(String fileName) {
        if (fileName != null) {
            int typeIndex = fileName.lastIndexOf(".");
            if (typeIndex != -1) {
                String fileType = fileName.substring(typeIndex + 1)
                        .toLowerCase();
                return fileType;
            }
        }
        return "";
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean checkUsagePermission(Context context) {
        AppOpsManager appOps = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOps != null) {
                int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(),
                        context.getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED;
            }
        }
        return false;
    }


    public static boolean canDrawOverlayViews(Context context) {
        if (Build.VERSION.SDK_INT < 21) {
            return true;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Settings.canDrawOverlays(context);
            } else {
                return true;
            }
        } catch (NoSuchMethodError e) {
            return canDrawOverlaysUsingReflection(context);
        }
    }

    public static boolean canDrawOverlaysUsingReflection(Context context) {
        try {
            AppOpsManager manager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

                Class clazz = AppOpsManager.class;
                Method dispatchMethod = clazz.getMethod("checkOp", new Class[]{int.class, int.class, String.class});
                //AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
                int mode = (Integer) dispatchMethod.invoke(manager, new Object[]{24, Binder.getCallingUid(), context.getApplicationContext().getPackageName()});

                return AppOpsManager.MODE_ALLOWED == mode;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /*public static void showSDCardPermissionDialog(Activity context, String path) {
        final Dialog deleteDialog = new Dialog(context, R.style.WideDialog);
        deleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        deleteDialog.setContentView(R.layout.layout_sdcard_permission_dialog);

        TextView description = deleteDialog.findViewById(R.id.title);
        description.setText(context.getString(R.string.needsaccesssummary) + path + context.getString(R.string.needsaccesssummary1));

        TextView actionOk = deleteDialog.findViewById(R.id.actionOk);
        TextView actionCancel = deleteDialog.findViewById(R.id.actionCancel);

        actionCancel.setOnClickListener(view -> deleteDialog.dismiss());

        actionOk.setOnClickListener(view -> {
            deleteDialog.dismiss();
            triggerStorageAccessFramework(context);
        });

        deleteDialog.show();
    }*/

    private static void triggerStorageAccessFramework(Activity context) {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivityForResult(intent, REQUEST_SDCARD_WRITE_PERMISSION);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int checkFSDCardPermission(final File folder, Activity context) {
        boolean lol = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP, ext = isOnExtSdCard(folder, context);
        if (lol && ext) {
            if (!folder.exists() || !folder.isDirectory()) {
                return 0;
            }

            // On Android 5, trigger storage access framework.
            if (!isWritableNormalOrSaf(folder, context)) {
                //showSDCardPermissionDialog(context, folder.getPath());
                return 2;
            }
            return 1;
        } else if (Build.VERSION.SDK_INT == 19 && isOnExtSdCard(folder, context)) {
            // Assume that Kitkat workaround works
            return 1;
        } else if (isWritable(new File(folder, "DummyFile"))) {
            return 1;
        } else {
            return 0;
        }
    }

    public static final boolean isWritableNormalOrSaf(final File folder, Context c) {
        // Verify that this is a directory.
        if (folder == null)
            return false;
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }

        // Find a non-existing manager in this directory.
        int i = 0;
        File file;
        do {
            String fileName = "AugendiagnoseDummyFile" + (++i);
            file = new File(folder, fileName);
        }
        while (file.exists());

        // First check regular writability
        if (isWritable(file)) {
            return true;
        }

        // Next check SAF writability.
        DocumentFile document = getDocumentFile(file, false, c);

        if (document == null) {
            return false;
        }

        // This should have created the manager - otherwise something is wrong with access URL.
        boolean result = document.canWrite() && file.exists();

        // Ensure that the dummy manager is not remaining.
        deleteFile(file, c);
        return result;
    }

    public static final boolean deleteFile(@NonNull final File file, Context context) {
        // First try the normal deletion.
        if (file == null) return true;
        boolean fileDelete = deleteFilesInFolder(file, context);
        if (file.delete() || fileDelete)
            return true;
        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(file, context)) {

            DocumentFile document = getDocumentFile(file, false, context);
            if (document != null) {
                return document.delete();
            } else {
                return false;
            }
        }

        // Try the Kitkat workaround.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ContentResolver resolver = context.getContentResolver();

            try {
                Uri uri = getUriFromFile(file.getAbsolutePath(), context);
                resolver.delete(uri, null, null);
                return !file.exists();
            } catch (Exception e) {
                Log.e("FileUtils", "Error when deleting manager " + file.getAbsolutePath(), e);
                return false;
            }
        }

        return !file.exists();
    }

    public static final boolean deleteFilesInFolder(final File folder, Context context) {
        boolean totalSuccess = true;
        if (folder == null)
            return false;
        if (folder.isDirectory()) {
            for (File child : folder.listFiles()) {
                deleteFilesInFolder(child, context);
            }

            if (!folder.delete())
                totalSuccess = false;
        } else {

            if (!folder.delete())
                totalSuccess = false;
        }
        return totalSuccess;
    }

    public static final boolean isWritable(final File file) {
        if (file == null)
            return false;
        boolean isExisting = file.exists();

        try {
            FileOutputStream output = new FileOutputStream(file, true);
            try {
                output.close();
            } catch (IOException e) {
                // do nothing.
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        boolean result = file.canWrite();

        // Ensure that manager is not created during this process.
        if (!isExisting) {
            file.delete();
        }

        return result;
    }

    public static boolean isOnExtSdCard(final File file, Context c) {
        return getExtSdCardFolder(file, c) != null;
    }

    public static String getExtSdCardFolder(final File file, Context context) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public static DocumentFile getDocumentFile(final File file, final boolean isDirectory, Context context) {
        String baseFolder = getExtSdCardFolder(file, context);
        boolean originalDirectory = false;
        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            Log.e("StorageUtils","fullPath: "+fullPath);
            if (!baseFolder.equals(fullPath))
                relativePath = fullPath.substring(baseFolder.length() + 1);
            else originalDirectory = true;
        } catch (IOException e) {
            return null;
        } catch (Exception f) {
            originalDirectory = true;
            //continue
        }

//        String as = "";
        String as = PreferencesManager.getSDCardTreeUri(context);

        Uri treeUri = null;
        if (!TextUtils.isEmpty(as)) treeUri = Uri.parse(as);
        if (treeUri == null) {
            return null;
        }

        Log.e("StorageUtils","treeUri: "+treeUri + " as: " +as);
        Log.e("StorageUtils","relativePath: "+relativePath);
        Log.e("StorageUtils","baseFolder: "+baseFolder);

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(context, treeUri);
        if (originalDirectory) return document;
        String[] parts = relativePath.split("\\/");

        Log.e("StorageUtils","parts: "+parts.toString());
        if (document != null) {
            for (int i = 0; i < parts.length; i++) {
                if (parts[i] != null) {
                    if (document != null) {
                        DocumentFile nextDocument = document.findFile(parts[i]);

                        if (nextDocument == null) {
                            if ((i < parts.length - 1) || isDirectory) {
                                nextDocument = document.createDirectory(parts[i]);
                            } else {
                                nextDocument = document.createFile("image", parts[i]);
                            }
                        }
                        document = nextDocument;
                    }
                }
            }
        }
        return document;
    }


    public static Uri getUriFromFile(final String path, Context context) {
        ContentResolver resolver = context.getContentResolver();

        Cursor filecursor = resolver.query(MediaStore.Files.getContentUri("external"),
                new String[]{BaseColumns._ID}, MediaStore.MediaColumns.DATA + " = ?",
                new String[]{path}, MediaStore.MediaColumns.DATE_ADDED + " desc");
        filecursor.moveToFirst();

        if (filecursor.isAfterLast()) {
            filecursor.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, path);
            return resolver.insert(MediaStore.Files.getContentUri("external"), values);
        } else {
            int imageId = filecursor.getInt(filecursor.getColumnIndex(BaseColumns._ID));
            Uri uri = MediaStore.Files.getContentUri("external").buildUpon().appendPath(
                    Integer.toString(imageId)).build();
            filecursor.close();
            return uri;
        }
    }

    public static int getBottomNavigationHeight(Context context) {
        Resources resources = context.getResources();
        if (hasNavigationBar(resources)) {
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }

        return 0;
    }

    private static boolean hasNavigationBar(Resources resources) {
        int hasNavBarId = resources.getIdentifier("config_showNavigationBar",
                "bool", "android");
        return hasNavBarId > 0 && resources.getBoolean(hasNavBarId);
    }

    public static boolean moveFile(final File source, final File target, Context context) {

        boolean isCopy = false;

        isCopy = copyFile(source, target, context);
//        if (source.isDirectory()) {
//            isCopy = copyFile(source, target, context);
//        } else {
//            String newPath = target.getPath() + "/" + source.getName();
//            File fileee = new File(newPath);
//            if (fileee.exists()) {
//                String[] separated = source.getName().split("\\.");
//                String name = separated[0];
//                String type = separated[1];
//
//                String newPath2 = target.getPath() + "/" + name + "_" + System.currentTimeMillis() + "." + type;
//                File file2 = new File(newPath2);
//                isCopy =  copyFile(source, file2, context);
//            } else {
//                isCopy =  copyFile(source, fileee, context);
//            }
//        }
        if (isCopy) {

            boolean isDelete = deleteFile(source, context);

            if (isDelete) {
                MediaScannerConnection.scanFile(context, new String[]{source.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                    }
                });
                return true;
            }
        }

        return false;
    }

    public static boolean renameFile(File file, String newName, Context context) {
        DocumentFile targetDocument = getDocumentFile(file, false, context);
        boolean renamed = targetDocument.renameTo(newName);

        Log.e("renameFileUtils", "ParentFile: " + targetDocument.getParentFile() + " Name: " + targetDocument.getName());

        return renamed;
    }


    public static boolean copyFile(final File source, final File target, Context context) {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdir();
            }

            try {
                String[] children = source.list();
                for (int i = 0; i < source.listFiles().length; i++) {

                    copyFile(new File(source, children[i]), new File(target, children[i]), context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            FileInputStream inStream = null;
            OutputStream outStream = null;
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                inStream = new FileInputStream(source);

// First try the normal way
                if (isWritable(target)) {
// standard way
                    outStream = new FileOutputStream(target);
                    inChannel = inStream.getChannel();
                    outChannel = ((FileOutputStream) outStream).getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
// Storage Access Framework
                        DocumentFile targetDocument = getDocumentFile(target, false, context);
                        outStream =
                                context.getContentResolver().openOutputStream(targetDocument.getUri());
                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
// Workaround for Kitkat ext SD card
                        Uri uri = getUriFromFile(target.getAbsolutePath(), context);
                        outStream = context.getContentResolver().openOutputStream(uri);
                    } else {
                        return false;
                    }

                    if (outStream != null) {
// Both for SAF and for Kitkat, write to output stream.
                        byte[] buffer = new byte[16384]; // MAGIC_NUMBER
                        int bytesRead;
                        while ((bytesRead = inStream.read(buffer)) != -1) {
                            outStream.write(buffer, 0, bytesRead);
                        }
                    }

                }

                MediaScannerConnection.scanFile(context, new String[]{target.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                    }
                });
            } catch (Exception e) {
                Log.e("FileUtils",
                        "Error when copying manager from " + source.getAbsolutePath() + " to " + target.getAbsolutePath(), e);
                return false;
            } finally {
                try {
                    inStream.close();
                } catch (Exception e) {
// ignore exception
                }
                try {
                    outStream.close();
                } catch (Exception e) {
// ignore exception
                }
                try {
                    inChannel.close();
                } catch (Exception e) {
// ignore exception
                }
                try {
                    outChannel.close();
                } catch (Exception e) {
// ignore exception
                }
            }
        }
        return true;

    }

    public static void getSdCardPath(File file, Context context) {
        DocumentFile targetDocument = getDocumentFile(file, false, context);

        Log.e("StorageUtils", " rename ParentFile: " + targetDocument.getParentFile() + " Name: " + targetDocument.getName());
    }


}
