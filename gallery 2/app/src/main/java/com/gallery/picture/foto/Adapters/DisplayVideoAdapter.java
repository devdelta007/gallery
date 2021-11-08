package com.gallery.picture.foto.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.gallery.picture.foto.Interface.VideoPreviousNext;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ui.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DisplayVideoAdapter extends PagerAdapter {

    List<PhotoData> imageList = new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;
    boolean isFirst = false;
    Handler h;
    Runnable run;
    private Runnable onEverySecond;
    VideoView videoView;
    VideoPreviousNext videoPreviousNext;

    public DisplayVideoAdapter(Context context, List<PhotoData> imageList, VideoPreviousNext videoPreviousNext) {
        this.context = context;
        this.imageList = imageList;
        this.videoPreviousNext = videoPreviousNext;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View itemView = layoutInflater.inflate(R.layout.item_video_play, container, false);

        RelativeLayout lout_center = itemView.findViewById(R.id.lout_center);
        CardView btnPlayPause = itemView.findViewById(R.id.btn_play_pause);
        ImageView ivCenterPlayPause = itemView.findViewById(R.id.iv_center_play_pause);
        ImageView previous = itemView.findViewById(R.id.previous);
        ImageView next = itemView.findViewById(R.id.next);
        ImageView ic_lock = itemView.findViewById(R.id.ic_lock);
        videoView = itemView.findViewById(R.id.video_view);
        SeekBar videoSeek = itemView.findViewById(R.id.video_seek);
        AppCompatTextView txtVideoCurrentDur = itemView.findViewById(R.id.txt_video_current_dur);
        AppCompatTextView txtVideoTotalDur = itemView.findViewById(R.id.txt_video_total_dur);
        LinearLayout lout_bottom = itemView.findViewById(R.id.lout_bottom);

        lout_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        run = new Runnable() {
            @Override
            public void run() {
//                Open_image_activity.toolbar.setVisibility(View.GONE);
                lout_center.setVisibility(View.GONE);
                VideoPlayActivity.toolbar.setVisibility(View.GONE);

            }
        };
        h = new Handler();


        onEverySecond = new Runnable() {
            @Override
            public void run() {
                if (videoSeek != null) {
                    videoSeek.setProgress(videoView.getCurrentPosition());
                    int duration = videoView.getCurrentPosition() / 1000;
                    int hours = duration / 3600;
                    int minutes = (duration / 60) - (hours * 60);
                    int seconds = duration - (hours * 3600) - (minutes * 60);
                    String formatted = String.format("%02d:%02d", minutes, seconds);
                    txtVideoCurrentDur.setText(formatted);
                }
//                if (videoView.isPlaying()) {
                videoSeek.postDelayed(onEverySecond, 1000);
//                }

                if (videoView.isPlaying()) {
                    ivCenterPlayPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                } else {
                    ivCenterPlayPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                }

            }
        };

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    ivCenterPlayPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                    videoView.pause();
                } else {
                    ivCenterPlayPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                    videoView.start();
                    h.removeCallbacks(run);
                    h.postDelayed(run, 2000);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPreviousNext.OnNext(position);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPreviousNext.OnPrevious(position);
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                ivCenterPlayPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
//                txtVideoCurrentDur.setText(getDurationString(videoView.getCurrentPosition()));
//                videoSeek.setMax(videoView.getDuration());
//                int duration = mp.getDuration() / 1000;
//                int hours = duration / 3600;
//                int minutes = (duration / 60) - (hours * 60);
//                int seconds = duration - (hours * 3600) - (minutes * 60);
//                String formatted = String.format("%02d:%02d", minutes, seconds);
//                txtVideoTotalDur.setText(formatted);
//                videoSeek.postDelayed(onEverySecond, 1000);
                ivCenterPlayPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                videoSeek.setMax(videoView.getDuration());
                int duration = mp.getDuration() / 1000;
                int hours = duration / 3600;
                int minutes = (duration / 60) - (hours * 60);
                int seconds = duration - (hours * 3600) - (minutes * 60);
                String formatted = String.format("%02d:%02d", minutes, seconds);
                txtVideoTotalDur.setText(formatted);
                videoSeek.postDelayed(onEverySecond, 1000);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ivCenterPlayPause.setVisibility(View.VISIBLE);
                ivCenterPlayPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                videoPreviousNext.OnNext(position);
            }
        });


        ic_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( videoSeek.getVisibility() == View.VISIBLE) {
                    ic_lock.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unlock));
                    videoSeek.setVisibility(View.INVISIBLE);
                    txtVideoCurrentDur.setVisibility(View.INVISIBLE);
                    lout_bottom.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    txtVideoTotalDur.setVisibility(View.INVISIBLE);
//                    ic_lock.setVisibility(View.INVISIBLE);
                    previous.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.INVISIBLE);
                    btnPlayPause.setVisibility(View.INVISIBLE);
//                    iv_rotate.setVisibility(View.INVISIBLE);
                    VideoPlayActivity.toolbar.setVisibility(View.GONE);
                } else {
                    ic_lock.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_loack));
                    videoSeek.setVisibility(View.VISIBLE);
                    lout_bottom.setBackground(context.getResources().getDrawable(R.drawable.bg_transparent_gradient));
                    txtVideoCurrentDur.setVisibility(View.VISIBLE);
                    txtVideoTotalDur.setVisibility(View.VISIBLE);
                    ic_lock.setVisibility(View.VISIBLE);
                    previous.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    btnPlayPause.setVisibility(View.VISIBLE);
                    VideoPlayActivity.toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ic_lock.getVisibility() == View.VISIBLE) {
//                    lout_center.setVisibility(View.GONE);
                    lout_bottom.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    videoSeek.setVisibility(View.INVISIBLE);
                    txtVideoCurrentDur.setVisibility(View.INVISIBLE);
                    txtVideoTotalDur.setVisibility(View.INVISIBLE);
                    ic_lock.setVisibility(View.INVISIBLE);
                    previous.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.INVISIBLE);
                    btnPlayPause.setVisibility(View.INVISIBLE);
//                    iv_rotate.setVisibility(View.INVISIBLE);
                    VideoPlayActivity.toolbar.setVisibility(View.GONE);
                } else {
                    videoSeek.setVisibility(View.VISIBLE);
                    txtVideoCurrentDur.setVisibility(View.VISIBLE);
                    lout_bottom.setBackground(context.getResources().getDrawable(R.drawable.bg_transparent_gradient));
                    txtVideoTotalDur.setVisibility(View.VISIBLE);
                    ic_lock.setVisibility(View.VISIBLE);
                    previous.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    btnPlayPause.setVisibility(View.VISIBLE);
                    VideoPlayActivity.toolbar.setVisibility(View.VISIBLE);

                }
            }
        });


        videoSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.start();
                ivCenterPlayPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                if (videoView.isPlaying()) {
                    videoSeek.postDelayed(onEverySecond, 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoView.pause();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration = videoView.getCurrentPosition() / 1000;
                int hours = duration / 3600;
                int minutes = (duration / 60) - (hours * 60);
                int seconds = duration - (hours * 3600) - (minutes * 60);
                String formatted = String.format("%02d:%02d", minutes, seconds);
                txtVideoCurrentDur.setText(formatted);
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }
        });


//        itemView.setOnClickListener(v -> imageToolbar.OnImageToolbar(v));

        videoSeek.setProgress(0);
        videoView.setVideoPath(imageList.get(position).getFilePath());
        videoView.requestFocus();
        videoView.start();

        container.addView(itemView);
        return itemView;
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }


    private String getDurationString(int duration) {

       /* long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));

        if (hours == 0) {
            return minutes + ":" + seconds;
        } else
            return hours + ":" + minutes + ":" + seconds;*/

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

    public void setvideoshow() {
        if (videoView != null) {
            videoView.seekTo(1);
        }
    }

    public void pausevideo() {
        if (videoView != null) {
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        }
    }
}
