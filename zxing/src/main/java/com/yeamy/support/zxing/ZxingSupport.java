package com.yeamy.support.zxing;

import android.app.Activity;
import android.view.TextureView;
import android.widget.CompoundButton;

import com.yeamy.support.zxing.camera.AutoFocusImpl;
import com.yeamy.support.zxing.camera.CameraImpl;
import com.yeamy.support.zxing.camera.ScanImpl;
import com.yeamy.support.zxing.decode.DecodeManager;
import com.yeamy.support.zxing.plugin.InactivityTimer;
import com.yeamy.support.zxing.plugin.ViewfinderView;

public class ZxingSupport implements CompoundButton.OnCheckedChangeListener {
    //request
    private LooperThread thread;
    private CameraImpl camera;
    private ScanImpl scan;
    private Viewfinder vf;
    private TextureView view;
    private ScanResultListener l;
    private InactivityTimer timer;

    private ViewfinderView viewfinderView;
    private CompoundButton torch;

    public ZxingSupport(Activity activity, ScanResultListener l, ViewfinderView view) {
        //init thread
        LooperThread thread = new LooperThread();
        thread.start();
        //init camera
        CameraImpl camera = new CameraImpl();
        //init scan & decode
        scan = new ScanImpl(new DecodeManager(thread));
        //init view & vf
        this.thread = thread;
        this.camera = camera;
        this.vf = view;
        this.viewfinderView = view;
        this.view = view.getTextureView();
        this.l = l;
        //
        timer = new InactivityTimer(activity, thread);
    }

    public void setTorch(CompoundButton torch) {
        this.torch = torch;
        initTorch();
    }

    private void initTorch() {
        if (camera.isOpen() && torch != null) {
            //init torch
            if (camera.supportTorch()) {
                torch.setOnCheckedChangeListener(this);
            } else {
                torch.setEnabled(false);
            }
        }
    }

    public void onResume() {
        timer.onResume();
        boolean isOpen = camera.open();
        initTorch();
        if (isOpen) {
            camera.setAutoFocusImpl(new AutoFocusImpl(thread));
            viewfinderView.setPreviewListener(new ViewfinderView.PreviewListener() {
                @Override
                public void onViewCreated() {
                    camera.requestPreview(view, vf);
                }

                @Override
                public void onStartPreview() {
                    scan.requestScan(camera, l);
                }
            });
        }
    }

    public void onPause() {
        camera.close();
        timer.onPause();
    }

    public void onDestroy() {
        thread.close();
    }

    @Override
    public void onCheckedChanged(CompoundButton torch, boolean isChecked) {
        camera.setTorch(isChecked);
    }

    public void requestScan() {
        scan.requestScan(camera, l);
    }
}