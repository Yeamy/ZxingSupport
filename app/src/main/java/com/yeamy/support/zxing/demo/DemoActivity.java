package com.yeamy.support.zxing.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ToggleButton;

import com.yeamy.support.zxing.ScanResult;
import com.yeamy.support.zxing.ZxingSupport;
import com.yeamy.support.zxing.plugin.BeepManager;
import com.yeamy.support.zxing.plugin.InactivityTimer;
import com.yeamy.support.zxing.plugin.ViewfinderView;

public class DemoActivity extends Activity implements ZxingSupport.Listener {
    //request
    private ZxingSupport zxing;
    //option
    private BeepManager beep;
    private InactivityTimer timer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);

        zxing = new ZxingSupport(this);
        zxing.setViewfinderView((ViewfinderView) findViewById(R.id.preview_view));
        zxing.setTorch((ToggleButton) findViewById(R.id.torch));

        beep = new BeepManager(R.raw.beep);
        timer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.onResume();
        beep.onResume(this);
        zxing.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.onPause();
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
    public void onScanInitReady() {
        zxing.requestScan();
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