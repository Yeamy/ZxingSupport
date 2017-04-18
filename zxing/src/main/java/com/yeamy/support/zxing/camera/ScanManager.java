package com.yeamy.support.zxing.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;

import com.yeamy.support.zxing.ScanResult;
import com.yeamy.support.zxing.ScanResultListener;
import com.yeamy.support.zxing.Viewfinder;
import com.yeamy.support.zxing.decode.DecodeBean;
import com.yeamy.support.zxing.decode.DecodeManager;
import com.yeamy.support.zxing.decode.DecodeManager.DecodeCallback;

@SuppressWarnings("deprecation")
public class ScanManager implements PreviewCallback, DecodeCallback {
    private final DecodeBean bean;
    private final DecodeManager decode;
    private ScanResultListener listener;
    private CameraImpl camera;
    private boolean ready = true;

    public ScanManager(DecodeManager manager) {
        decode = manager;
        bean = new DecodeBean();
    }

    public boolean requestScan(CameraImpl camera, ScanResultListener listener) {
        this.camera = camera;
        this.listener = listener;
        if (camera.isPreviewing() && ready) {
            camera.startAutoFocus();
            startScan();
            ready = false;
            return true;
        }
        return false;
    }

    private void startScan() {
        if (camera.isOpen()) {
            camera.device.setOneShotPreviewCallback(this);
        }
    }

    @Override
    public final void onPreviewFrame(byte[] data, Camera camera) {
        // Size s = camera.getParameters().getPreviewSize();
        // System.out.println("!!!==============> " + s.width + " " + s.height);
        Size size = this.camera.previewSize;
        int dataWidth = size.width;
        int dataHeight = size.height;
        bean.setSource(data, dataWidth, dataHeight);
        // frame
        int left, top, width, height;
        Viewfinder viewfinder = this.camera.viewfinder;
        Point view = viewfinder.getPreviewSize();
        Rect frame = viewfinder.getFrameSize();

        boolean portrait = viewfinder.getOrientation() % 180 != 0;
        if (portrait) {// portrait
            left = frame.top * dataWidth / view.y;
            top = (view.x - frame.right) * dataWidth / view.y;
            width = frame.height() * dataWidth / view.y;
            height = frame.width() * dataWidth / view.y;
            bean.setFrameRectPortrait(left, top, width, height);
        } else {// landspace
            left = frame.left * dataWidth / view.x;
            top = frame.top * dataWidth / view.x;
            width = frame.width() * dataWidth / view.x;
            height = frame.height() * dataWidth / view.x;
            bean.setFrameRectLandspace(left, top, width, height);
        }
        // done
        decode.requestDecode(bean, this, viewfinder);// jump to decode
    }

    @Override
    public final void onDecodeSuccess(ScanResult result) {
        if (listener != null) {
            listener.onScanSuccess(result);
            listener = null;
        }
        camera.stopAutoFocus();
        bean.clearBuff();
        camera = null;
        ready = true;
    }

    @Override
    public void onDecodeFail() {
        startScan();
    }

}
