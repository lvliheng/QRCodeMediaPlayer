package com.demo.henry.qrcodemediaplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    public static final int REQUEST_SCAN_CODE = 0;
    public static final int REQUEST_PERMISSION_CAMERA = 1;
    public static final String EXTRA_URL = "url";

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Button scan_btn = findViewById(R.id.scan_btn);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        Button media_player = findViewById(R.id.media_player);
        Button video_view = findViewById(R.id.video_view);
        Button browser = findViewById(R.id.browser);
        scan_btn.setOnClickListener(this);
        media_player.setOnClickListener(this);
        video_view.setOnClickListener(this);
        browser.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("MainActivity", "onActivityResult: " + requestCode + "  " + resultCode);
        if (requestCode == REQUEST_SCAN_CODE) {
            if (resultCode == RESULT_OK) {
                String url = "";
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        url = bundle.getString(EXTRA_URL, "");
                    }
                }
                resultTextView.setText(url);
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        String url = resultTextView.getText().toString();
        switch (view.getId()) {
            case R.id.scan_btn:
                if (!isPermissionEnabled(MainActivity.this, Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA)) {
                    return;
                }
                intent.setClass(MainActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_SCAN_CODE);
                intent = null;
                break;
            case R.id.media_player:
                intent.setClass(MainActivity.this, MediaPlayerActivity.class);
                intent.putExtra(EXTRA_URL, url);
                break;
            case R.id.video_view:
                intent.setClass(MainActivity.this, VideoViewActivity.class);
                intent.putExtra(EXTRA_URL, url);
                break;
            case R.id.browser:
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_PERMISSION_CAMERA == requestCode && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ScanActivity.class);
            startActivityForResult(intent, REQUEST_SCAN_CODE);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
