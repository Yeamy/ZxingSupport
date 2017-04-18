package com.yeamy.support.zxing;

import android.widget.CompoundButton;

import com.yeamy.support.zxing.camera.AutoFocusManager;
import com.yeamy.support.zxing.camera.CameraImpl;
import com.yeamy.support.zxing.camera.ScanManager;
import com.yeamy.support.zxing.decode.DecodeManager;
import com.yeamy.support.zxing.plugin.ViewfinderView;

public class ZxingSupport {
    //request
    private LooperThread thread;
    private CameraImpl camera;
    private ScanManager scan;
    private Listener l;

    private CompoundButton torch;

    public ZxingSupport(Listener l) {
        //init thread
        LooperThread thread = new LooperThread();
        thread.start();
        //init camera
        CameraImpl camera = new CameraImpl();
        //init scan & decode
        scan = new ScanManager(new DecodeManager(thread));
        //init view & vf
        this.thread = thread;
        this.camera = camera;
        this.l = l;
    }

    public void setViewfinderView(ViewfinderView viewfinderView) {
        viewfinderView.setPreviewListener(callback);
    }

    private Callback callback = new Callback();

    private class Callback implements ViewfinderView.PreviewListener,
            CompoundButton.OnCheckedChangeListener {

        @Override
        public void onViewCreated() {
            l.onScanInitReady();
        }

        @Override
        public void onStartPreview() {
            scan.requestScan(camera, l);
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
            } else {
                torch.setEnabled(false);
            }
        }
    }

    public void onResume() {
        if (camera.open()) {
            camera.setAutoFocus(new AutoFocusManager(thread));
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
        scan.requestScan(camera, l);
    }

    public interface Listener extends ScanResultListener {
        void onScanInitReady();
    }
}