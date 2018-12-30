package com.demo.henry.qrcodemediaplayer;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MediaPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private static final String TAG = "MainActivity";

    public static final int HANDLER_BACK = 0;
    public static final int HANDLER_FINISH = 1;
    public static final int HANDLER_AUTO_CLOSE_LEFT = 2;
    public static final int HANDLER_AUTO_CLOSE_RIGHT = 3;
    public static final int AUTO_CLOSE_DELAY = 5000;
    public static final int FINISH_DELAY = 2000;
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_URL = "url";

    public static final String DEFAULT_VIDEO_URL = "https://www.apple.com/105/media/cn/mac/family/2018/46c4b917_abfd_45a3_9b51_4e3054191797/films/bruce/mac-bruce-tpl-cn-2018_1280x720h.mp4";

    private MediaPlayer mediaPlayer;

    private SurfaceView video_sv;
    private ProgressBar progressBar;

    private MyHandler handler;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideVirtualButton();
        initView();
        initPlayer();
        initData();
    }

    private void hideVirtualButton() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void initView() {
        video_sv = findViewById(R.id.video_sv);
        SurfaceHolder surfaceHolder = video_sv.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

        progressBar = findViewById(R.id.progressBar);
    }

    private void initData() {
        WindowManager windowManager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        video_sv.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));

        handler = new MyHandler(MediaPlayerActivity.this);
        toast = Toast.makeText(MediaPlayerActivity.this, R.string.app_name, Toast.LENGTH_SHORT);
    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);

        String currUrl = DEFAULT_VIDEO_URL;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currUrl = bundle.getString(EXTRA_URL, DEFAULT_VIDEO_URL);
        }
        try {
            mediaPlayer.setDataSource(currUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceCreated: ");
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i(TAG, "surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceDestroyed: ");
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

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        Log.i(TAG, "onInfo: i: " + i + " i1: " + i1);
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Log.i(TAG, "onBufferingUpdate: " + i);
    }

    private static class MyHandler extends Handler{

        WeakReference<MediaPlayerActivity> weakReference;

        private MyHandler(MediaPlayerActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MediaPlayerActivity mediaPlayerActivity = weakReference.get();
            switch (msg.what) {
                case HANDLER_BACK:
                    if (hasMessages(HANDLER_FINISH)) {
                        mediaPlayerActivity.finish();
                    } else {
                        if (mediaPlayerActivity.toast != null) {
                            mediaPlayerActivity.toast.setText("");
                            mediaPlayerActivity.toast.show();
                        }
                        sendEmptyMessageDelayed(HANDLER_FINISH, FINISH_DELAY);
                    }
                    break;
                case HANDLER_FINISH:
                    removeMessages(HANDLER_FINISH);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }
}
