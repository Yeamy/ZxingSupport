package com.yeamy.support.zxing.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.yeamy.support.zxing.ScanResult;
import com.yeamy.support.zxing.ZxingSupport;
import com.yeamy.support.zxing.decode.DecodeRequest;
import com.yeamy.support.zxing.plugin.BeepManager;
import com.yeamy.support.zxing.plugin.InactivityTimer;
import com.yeamy.support.zxing.plugin.ViewfinderView;

public class DemoActivity extends Activity implements ZxingSupport.Listener {
    //request
    private ZxingSupport zxing;
    //option
    private BeepManager beep;// beep when scan result
    private InactivityTimer timer;//auto close activity after 15-minutes

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);

        DecodeRequest request = new DecodeRequest();//需要判断的类型，此处选择默认类型
        zxing = new ZxingSupport(this, request);
        zxing.setViewfinderView((ViewfinderView) findViewById(R.id.preview_view));//设置预览
        zxing.setTorch((ToggleButton) findViewById(R.id.torch));//设置闪光灯

        beep = new BeepManager(R.raw.beep);
        timer = new InactivityTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.onResume(this, InactivityTimer.INACTIVITY_DELAY_MS);
        beep.onResume(this);
        zxing.onResume();

        if (!zxing.isOpen()) {//处理启动失败
            new OnFailDialog(this).show();
        }
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
    public void onScanReady() {
        zxing.requestScan();
    }

    @Override
    public void onScanSuccess(ScanResult result) {
        beep.play();
        Dialog dialog = new ScanResultDialog(this, result.getRawText()).show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                zxing.requestScan();
            }
        });//再次扫描
        System.out.println(result.getRawText());
        // TODO Auto-generated method stub
        Toast.makeText(this, result.getRawText(), Toast.LENGTH_SHORT).show();
    }

}