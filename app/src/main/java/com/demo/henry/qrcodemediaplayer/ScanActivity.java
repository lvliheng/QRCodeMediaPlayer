package com.demo.henry.qrcodemediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

/**
 * Created by lvliheng on 2018/10/31 at 14:08.
 */
public class ScanActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {


    private QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initView();
    }

    private void initView() {
        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Log.e("ScanActivity", "onQRCodeRead: " + text);
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_URL, text);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
