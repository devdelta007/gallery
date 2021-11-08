package com.gallery.picture.foto.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gallery.picture.foto.Adapters.DisplayVideoAdapter;
import com.gallery.picture.foto.Interface.VideoPreviousNext;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.ui.StorageActivity;
import com.gallery.picture.foto.utils.Constant;
import com.gallery.picture.foto.utils.CustomViewPager;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.RxBus;
import com.gallery.picture.foto.utils.StorageUtils;
import com.gallery.picture.foto.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class VideoPlayActivity extends AppCompatActivity implements VideoPreviousNext {

    String duration;
    ImageView ivBack;
    TextView txtTitle;
    ImageView ivMore;


    public static RelativeLayout toolbar;
    CustomViewPager viewPager;

    int currentDur = 0;

    Handler mHandler = new Handler();
    boolean isFirstTime = true;

    boolean isCustomChangeSeekbar = false;

    int position = -1;
    List<PhotoData> displayImageList = new ArrayList<>();
    DisplayVideoAdapter adapter;

    @Override
    public void OnPrevious(int currentPos) {
        if (position != 0) {
            position--;
            if (viewPager != null) {
                viewPager.setCurrentItem(position, true);
            }
        }

    }

    @Override
    public void OnNext(int currentPos) {
        if (position != displayImageList.size() - 1) {

            position++;

            if (viewPager != null) {
                viewPager.setCurrentItem(position, true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ivBack = findViewById(R.id.iv_back);
        txtTitle = findViewById(R.id.txt_title);
        ivMore = findViewById(R.id.iv_more);
        txtTitle = findViewById(R.id.txt_title);
        viewPager = findViewById(R.id.view_pager);

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_video_play);

        ivBack = findViewById(R.id.iv_back);

        toolbar = findViewById(R.id.toolbar);
        intView();
        copyMoveEvent();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreMenu();
            }
        });
    }


    private void intView() {
        Intent intent = getIntent();

        displayImageList.addAll(Constant.displayVideoList);
        if (intent != null) {

            position = intent.getIntExtra("pos", 0);
//            filePath = intent.getStringExtra("FilePath");
//            filename = intent.getStringExtra("FileName");
            // duration = intent.getStringExtra("Duration");

//            txtTitle.setText(filename);
//            txtTitle.setText(displayImageList.get(position).getFileName());
            try {

                adapter = new DisplayVideoAdapter(this, displayImageList, this);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(position);
                viewPager.setOffscreenPageLimit(0);

                viewPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float positionOffset, int positionOffsetPixels) {
                        toolbar.setVisibility(View.VISIBLE);
                        position = i;
                        txtTitle.setText(displayImageList.get(position).getFileName());
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

           /* int pos = position;
            pos++;
            txtTitle.setText(pos + "/" + displayImageList.size());*/
                txtTitle.setText(displayImageList.get(position).getFileName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            adapter.setvideoshow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


    private void showMoreMenu() {

        PopupMenu popup = new PopupMenu(this, ivMore);
        popup.getMenuInflater().inflate(R.menu.more_menu_video, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_delete:
                        deleteDialog();
                        break;
                    case R.id.menu_copy:
                        Constant.isCopyData = true;
                        setCopyMoveOptinOn();
                        break;
                    case R.id.menu_move:
                        Constant.isCopyData = false;
                        setCopyMoveOptinOn();
                        break;

                    case R.id.menu_details:
                        showDetailDialog();
                        break;

                }
                return false;
            }
        });
        popup.show();
    }


    private void setCopyMoveOptinOn() {


        Constant.pastList = new ArrayList<>();

        String sdCardPath = Utils.getExternalStoragePath(this, true);

        boolean isSdcardPath = false;

        File file = new File(displayImageList.get(viewPager.getCurrentItem()).getFilePath());
        if (file.exists()) {
            Constant.pastList.add(file);
            if (!isSdcardPath) {
                if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
                    if (file.getPath().contains(sdCardPath)) {
                        isSdcardPath = true;
                    }
                }
            }

        }

        Constant.isFileFromSdCard = isSdcardPath;
        Intent intent = new Intent(VideoPlayActivity.this, StorageActivity.class);
        intent.putExtra("type", "CopyMove");
        setResult(RESULT_OK);
        startActivity(intent);

    }


    private void deleteDialog() {
        sdCardPermissionType = 1;
        if (adapter != null)
            adapter.pausevideo();
        int i = 0;
        i = viewPager.getCurrentItem();

        AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayActivity.this);
        builder.setMessage("Are you sure do you want to delete it?");
        builder.setCancelable(false);

        int finalI = i;
        builder.setPositiveButton(Html.fromHtml("<font color='#ffba00'>Yes</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();
                String sdCardPath = Utils.getExternalStoragePath(VideoPlayActivity.this, true);
                if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
                    if (displayImageList.get(finalI).getFilePath().contains(sdCardPath)) {
                        File file = new File(sdCardPath);
                        int mode = StorageUtils.checkFSDCardPermission(file, VideoPlayActivity.this);
                        if (mode == 2) {
                            Toast.makeText(VideoPlayActivity.this, "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
                        } else {
                            setDeleteFile();
                        }
                    } else {
                        setDeleteFile();
                    }
                } else {
                    setDeleteFile();
                }
//                setDeleteFile();

            }
        });

        builder.setNegativeButton(Html.fromHtml("<font color='#ffba00'>No</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                /*if (isSlideshow) {
                    if (isSlideshowStart) {
                        startSlideshow();
                    }
                }*/
            }
        });

        builder.show();

    }

    int sdCardPermissionType = 0;

    private void setDeleteFile() {
        sdCardPermissionType = 1;
        int finalI = viewPager.getCurrentItem();
        ArrayList<String> deleteList = new ArrayList<>();

        File file1 = new File(displayImageList.get(viewPager.getCurrentItem()).getFilePath());
        int mode = StorageUtils.checkFSDCardPermission(file1, VideoPlayActivity.this);
        if (mode == 2) {
            Toast.makeText(VideoPlayActivity.this, "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
        } else {
            boolean isDelete = StorageUtils.deleteFile(file1, VideoPlayActivity.this);

            if (isDelete) {
                MediaScannerConnection.scanFile(VideoPlayActivity.this, new String[]{file1.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                    }
                });

                displayImageList.remove(viewPager.getCurrentItem());
                adapter.notifyDataSetChanged();

                adapter = new DisplayVideoAdapter(VideoPlayActivity.this, displayImageList, VideoPlayActivity.this);
                viewPager.setAdapter(adapter);

                RxBus.getInstance().post(new DisplayDeleteEvent(deleteList));

                if (finalI < displayImageList.size() - 1) {
                    position = finalI;
                    viewPager.setCurrentItem(finalI);


                } else {
                    if (displayImageList.size() == 0) {
                        onBackPressed();
                    } else {
                        try {
                            viewPager.setCurrentItem(finalI - 1);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                deleteList.add(file1.getPath());

                RxBus.getInstance().post(new DisplayDeleteEvent(deleteList));


            } else {
                Toast.makeText(VideoPlayActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StorageUtils.REQUEST_SDCARD_WRITE_PERMISSION) {
            String p = PreferencesManager.getSDCardTreeUri(VideoPlayActivity.this);
            Uri oldUri = null;
            if (p != null) oldUri = Uri.parse(p);
            Uri treeUri = null;
            if (resultCode == Activity.RESULT_OK) {
                treeUri = data.getData();
                if (treeUri != null) {
                    PreferencesManager.setSDCardTreeUri(VideoPlayActivity.this, treeUri.toString());
                    if (sdCardPermissionType == 1) {
                        setDeleteFile();
                    }
                }
            }
            if (resultCode != Activity.RESULT_OK) {
                if (treeUri != null) {
                    PreferencesManager.setSDCardTreeUri(VideoPlayActivity.this, oldUri.toString());
//                    deleteFile();
                    if (sdCardPermissionType == 1) {
                        setDeleteFile();
                    }
                }
                return;
            }
            try {
                final int takeFlags = data.getFlags()
                        & ( Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDetailDialog() {
        if (adapter != null)
            adapter.pausevideo();
        final Dialog dialog = new Dialog(VideoPlayActivity.this, R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_details);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        TextView txt_title = dialog.findViewById(R.id.txt_title);
        TextView txt_format = dialog.findViewById(R.id.txt_format);
        TextView txt_time = dialog.findViewById(R.id.txt_time);
        TextView txt_resolution = dialog.findViewById(R.id.txt_resolution);
        TextView txt_file_size = dialog.findViewById(R.id.txt_file_size);
        TextView txt_duration = dialog.findViewById(R.id.txt_duration);
        TextView txt_path = dialog.findViewById(R.id.txt_path);


        TextView btn_ok = dialog.findViewById(R.id.btn_ok);
        txt_title.setText(displayImageList.get(viewPager.getCurrentItem()).getFileName());


        File file = new File(displayImageList.get(viewPager.getCurrentItem()).getFilePath());
        if (file.exists()) {
            String type = Utils.getMimeTypeFromFilePath(displayImageList.get(viewPager.getCurrentItem()).getFilePath());

            txt_format.setText(type);

            txt_file_size.setText(Formatter.formatShortFileSize(this, file.length()));

            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm a");
            String strDate = format.format(file.lastModified());

            txt_time.setText(strDate);

            txt_duration.setText(duration);

            try {
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(displayImageList.get(viewPager.getCurrentItem()).getFilePath());
                int height = Integer.parseInt(Objects.requireNonNull(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
                int width = Integer.parseInt(Objects.requireNonNull(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));

                txt_resolution.setText(width + "X" + height);
                txt_resolution.setVisibility(View.VISIBLE);

                if (duration == null && !duration.equalsIgnoreCase("")) {
                    String time = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMillisec = Long.parseLong(time);

                    String strDuration = getDurationString((int) timeInMillisec);
                    txt_duration.setText(strDuration);

                    txt_resolution.setText(width + "X" + height);
                }

            } catch (Exception e) {
                e.printStackTrace();
                txt_resolution.setVisibility(View.GONE);

            }


        }


        txt_path.setText(displayImageList.get(viewPager.getCurrentItem()).getFilePath());


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private String getDurationString(int duration) {

        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));

        String strMin = "";
        if (minutes < 10) {
            strMin = "0" + minutes;
        } else {
            strMin = String.valueOf(minutes);
        }

        String strSec = "";
        if (seconds < 10) {
            strSec = "0" + seconds;
        } else {
            strSec = String.valueOf(seconds);
        }

        String strHour = "";
        if (hours < 10) {
            strHour = "0" + hours;
        } else {
            strHour = String.valueOf(hours);
        }

        if (hours == 0) {
            return strMin + ":" + strSec;
        } else
            return strHour + ":" + strMin + ":" + strSec;


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void copyMoveEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(CopyMoveEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<CopyMoveEvent>() {
            @Override
            public void call(CopyMoveEvent event) {
                if (event.getCopyMoveList() != null && event.getCopyMoveList().size() != 0) {

                }

                if (event.getDeleteList() != null && event.getDeleteList().size() != 0) {
                    ArrayList<String> deleteList = new ArrayList<>();
                    deleteList = event.getDeleteList();

                    if (deleteList != null && deleteList.size() != 0)
                        for (int i = 0; i < deleteList.size(); i++) {

                            try {
                                if (deleteList.get(i).equalsIgnoreCase(displayImageList.get(viewPager.getCurrentItem()).getFilePath())) {
                                    int finalI = viewPager.getCurrentItem();

                                    displayImageList.remove(viewPager.getCurrentItem());
                                    adapter.notifyDataSetChanged();

                                    adapter = new DisplayVideoAdapter(VideoPlayActivity.this, displayImageList, VideoPlayActivity.this);
                                    viewPager.setAdapter(adapter);

                                    if (finalI < displayImageList.size() - 1) {
                                        position = finalI;
                                        viewPager.setCurrentItem(finalI);

                                    } else {
                                        if (displayImageList.size() == 0) {
                                            onBackPressed();
                                        } else {
                                            try {
                                                viewPager.setCurrentItem(finalI - 1);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
        RxBus.getInstance().addSubscription(this, subscription);
    }
}
