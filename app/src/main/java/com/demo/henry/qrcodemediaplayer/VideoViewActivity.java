package com.demo.henry.qrcodemediaplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by lvliheng on 2018/7/18 at 16:11.
 */
public class VideoViewActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {

    private static final String TAG = "VideoViewActivity";

    public static final String EXTRA_URL = "url";

    public static final String DEFAULT_VIDEO_URL = "https://www.apple.com/105/media/cn/mac/family/2018/46c4b917_abfd_45a3_9b51_4e3054191797/films/bruce/mac-bruce-tpl-cn-2018_1280x720h.mp4";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        initView();
    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        VideoView video_view = findViewById(R.id.video_view);

        video_view.setOnCompletionListener(this);
        video_view.setOnPreparedListener(this);
        video_view.setOnErrorListener(this);

        String url = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString(EXTRA_URL, DEFAULT_VIDEO_URL);
        }

        video_view.setVideoURI(Uri.parse(url));
        video_view.requestFocus();
        video_view.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i(TAG, "onCompletion: ");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.i(TAG, "onPrepared: ");
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.e(TAG, "onError: i: " + i + " i1: " + i1);
        Toast.makeText(this, i + "   " + i1, Toast.LENGTH_SHORT).show();
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        return false;
    }
}
