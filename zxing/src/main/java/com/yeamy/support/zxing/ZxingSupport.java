package com.yeamy.support.zxing;

import android.widget.CompoundButton;

import com.yeamy.support.zxing.camera.AutoFocusManager;
import com.yeamy.support.zxing.camera.CameraImpl;
import com.yeamy.support.zxing.camera.ScanManager;
import com.yeamy.support.zxing.decode.DecodeRequest;
import com.yeamy.support.zxing.plugin.ViewfinderView;

public class ZxingSupport {
    //request
    private LooperThread thread;
    private CameraImpl camera;
    private ScanManager scan;
    private Listener l;

    private ViewfinderView viewfinderView;
    private CompoundButton torch;

    public ZxingSupport(Listener l, DecodeRequest decode) {
        //init thread
        LooperThread thread = new LooperThread();
        thread.start();
        //init camera
        CameraImpl camera = new CameraImpl();
        //init scan & decode
        scan = new ScanManager(thread, camera, l, decode);
        //init view & vf
        this.thread = thread;
        this.camera = camera;
        this.l = l;
    }

    public void setViewfinderView(ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
        startPreview();
    }

    private void startPreview() {
        CameraImpl camera = this.camera;
        ViewfinderView view = this.viewfinderView;
        if (camera != null && camera.isOpen() && view != null) {
            viewfinderView.setPreviewListener(callback);
            camera.requestPreview(view.getTextureView(), view);
        }
    }

    private Callback callback = new Callback();

    private class Callback implements ViewfinderView.PreviewListener,
            CompoundButton.OnCheckedChangeListener {

        @Override
        public void onViewCreated() {
            startPreview();
        }

        @Override
        public void onStartPreview() {
            l.onScanReady();
        }

        @Override
        public void onCheckedChanged(CompoundButton torch, boolean isChecked) {
            camera.setTorch(isChecked);
        }
    }

    public void setTorch(CompoundButton torch) {
        this.torch = torch;
        initTorch();
    }

    private void initTorch() {
        if (camera.isOpen() && torch != null) {
            //init torch
            if (camera.supportTorch()) {
                torch.setOnCheckedChangeListener(callback);
                if (torch.isChecked()) camera.setTorch(true);
            } else {
                torch.setEnabled(false);
            }
        }
    }

    public void onResume() {
        if (camera.open()) {
            camera.setAutoFocus(new AutoFocusManager(thread));
            startPreview();
        }
        initTorch();
    }

    public void onPause() {
        camera.close();
    }

    public void onDestroy() {
        thread.close();
    }

    public void requestScan() {
        scan.requestScan();
    }

    public interface Listener extends ScanResultListener {
        void onScanReady();
    }
}