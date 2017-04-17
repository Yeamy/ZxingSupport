package com.yeamy.support.zxing.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ToggleButton;

import com.yeamy.support.zxing.ScanResult;
import com.yeamy.support.zxing.ScanResultListener;
import com.yeamy.support.zxing.ZxingSupport;
import com.yeamy.support.zxing.plugin.BeepManager;
import com.yeamy.support.zxing.plugin.ViewfinderView;

public class DemoActivitySupport extends Activity implements ScanResultListener {
    //request
    private ZxingSupport zxing;
    //option
    private BeepManager beep;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        ViewfinderView view = (ViewfinderView) findViewById(R.id.preview_view);

        zxing = new ZxingSupport(this, this, view);
        beep = new BeepManager(R.raw.beep);
        zxing.setTorch((ToggleButton) findViewById(R.id.torch));
    }

    @Override
    protected void onResume() {
        super.onResume();
        beep.onResume(this);
        zxing.onResume();
        zxing.requestScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beep.onPause();
        zxing.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beep.close();
        zxing.onDestroy();
    }

    @Override
    public void onScanSuccess(ScanResult result) {
        beep.play();
        Dialog dialog = new ResultDialog(this, result.getRawText()).show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                zxing.requestScan();
            }
        });
        System.out.println(result.getRawText());
        // TODO Auto-generated method stub
    }

}