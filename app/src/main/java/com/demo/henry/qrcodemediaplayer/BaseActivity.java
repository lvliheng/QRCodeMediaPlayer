package com.demo.henry.qrcodemediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lvliheng on 2017/7/6 下午6:10.
 */

public class BaseActivity extends Activity {


    public static final int PERMISSION_CAMERA = 0x110;
    public static final int PERMISSION_GALLERY = 0x119;
    public static final int PERMISSION_LOCATION = 0x120;
    public static final int PERMISSION_WRITE_EXTERNAL = 0x123;
    public static final int PERMISSION_DIAL = 0x124;

    public static final String DATA_ERROR = "数据异常";

    private AnimationCallBack animationCallBack;

    private int screenWidth;

    private static final int STOP_ANIMATION = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        init();
    }

    private void init() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
    }

    //提示信息动画
    public void startAnimation(TextView tipView, String tips) {
        startAnimation(null, tipView, tips, null);
    }

    public void startAnimation(TextView tipView, String tips, AnimationCallBack callBack) {
        startAnimation(null, tipView, tips, callBack);
    }

    public void startAnimation(View animationView, TextView tipView, String tips) {
        startAnimation(animationView, tipView, tips, null);
    }

    public void startAnimation(final View animationView, final TextView tipView, final String tips, AnimationCallBack callBack) {

        if (null == tipView) {
            return;
        }

        animationCallBack = callBack;

        TranslateAnimation translateAnimation = null;
        if (null != animationView) {
            translateAnimation = new TranslateAnimation(-10, 10, 1, 1);
            translateAnimation.setDuration(5);
            translateAnimation.setRepeatCount(15);
            translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            translateAnimation.setFillAfter(false);
            animationView.startAnimation(translateAnimation);
        }

        final AnimationSet animationSet = new AnimationSet(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setInterpolator(new DecelerateInterpolator());
        alphaAnimation.setFillAfter(true);
        animationSet.addAnimation(alphaAnimation);

        final TranslateAnimation tipTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        tipTranslateAnimation.setDuration(500);
        tipTranslateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        tipTranslateAnimation.setFillAfter(true);
        animationSet.addAnimation(tipTranslateAnimation);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Message message = new Message();
                message.what = STOP_ANIMATION;
                message.obj = tipView;
                handler.sendMessageDelayed(message, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (null != translateAnimation) {
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tipView.startAnimation(animationSet);
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setText(tips);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            tipView.startAnimation(animationSet);
            tipView.setVisibility(View.VISIBLE);
            tipView.setText(tips);
        }

    }

    private void stopAnimation(final View tipView) {

        if (null == tipView) {
            return;
        }

        AnimationSet animationSet = new AnimationSet(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(true);
        animationSet.addAnimation(alphaAnimation);

        final TranslateAnimation stopTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
        stopTranslateAnimation.setDuration(500);
        stopTranslateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        stopTranslateAnimation.setFillAfter(true);
        animationSet.addAnimation(stopTranslateAnimation);
        tipView.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tipView.setVisibility(View.GONE);
                tipView.clearAnimation();
                if (null != animationCallBack) {
                    animationCallBack.animationCallBack();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == STOP_ANIMATION) {
                stopAnimation((View)msg.obj);
            }
        }
    };

    public interface AnimationCallBack{
        void animationCallBack();
    }

    private void goToSettings(int requestCode) {
        String str;
        switch (requestCode) {
            case PERMISSION_CAMERA:
                str = getString(R.string.permission_camera_tip);
                break;
            case PERMISSION_GALLERY:
                str = getString(R.string.permission_gallery_tip);
                break;
            case PERMISSION_LOCATION:
                str = getString(R.string.permission_location_tip);
                break;
            case PERMISSION_WRITE_EXTERNAL:
                str = getString(R.string.permission_write_external_tip);
                break;
            case PERMISSION_DIAL:
                str = getString(R.string.permission_dial_tip);
                break;
            default:
                str = getString(R.string.permission_default_tip);
                break;
        }
        if (!BaseActivity.this.isFinishing()) {
            Toast.makeText(BaseActivity.this, str, Toast.LENGTH_SHORT).show();
        }
        if (requestCode != PERMISSION_WRITE_EXTERNAL) {
            Intent myAppSettings = new Intent();
            myAppSettings.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
            myAppSettings.setData(Uri.parse("package:" + getPackageName()));
            myAppSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myAppSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            myAppSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivityForResult(myAppSettings, requestCode);
        }

    }

    public boolean isPermissionEnabled(Activity activity, String permission, int requestCode) {
        boolean result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(activity,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        permission)) {
                    result = false;
                    goToSettings(requestCode);
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{permission},
                            requestCode);
                    result = false;
                }
            } else {
                result = true;
            }
        } else {
            result = true;
        }
        return result;
    }

    private String getImageCode(File file) {
        Bitmap rawTakenImage = BitmapFactory.decodeFile(file.getAbsolutePath());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        rawTakenImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        Uri resizedUri = Uri.fromFile(file);
        File resizedFile = new File(resizedUri.getPath());
        try {
            FileOutputStream fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }


}
