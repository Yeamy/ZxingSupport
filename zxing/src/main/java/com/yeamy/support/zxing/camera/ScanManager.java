package com.yeamy.support.zxing.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;

import com.yeamy.support.zxing.LooperThread;
import com.yeamy.support.zxing.ScanResult;
import com.yeamy.support.zxing.ScanResultListener;
import com.yeamy.support.zxing.Viewfinder;
import com.yeamy.support.zxing.decode.DecodeBean;
import com.yeamy.support.zxing.decode.DecodeRequest;
import com.yeamy.support.zxing.decode.DecodeRequest.DecodeCallback;

@SuppressWarnings("deprecation")
public class ScanManager implements PreviewCallback, DecodeCallback {
    private final DecodeBean bean;
    private final DecodeRequest decode;
    private ScanResultListener listener;
    private CameraImpl camera;
    private LooperThread thread;
    private boolean ready = true;

    public ScanManager(LooperThread thread, CameraImpl camera, ScanResultListener listener,
                       DecodeRequest decode) {
        this.thread = thread;
        this.camera = camera;
        this.listener = listener;
        this.decode = decode;
        this.bean = new DecodeBean();
    }

    public boolean requestScan() {
        PreviewManager pm = camera.getPreviewManager();
        if (pm.isPreviewing() && ready) {
            camera.startAutoFocus();
            startScan();
            ready = false;
            return true;
        }
        return false;
    }

    private void startScan() {
        if (camera.isOpen()) {
            camera.getDevice().setOneShotPreviewCallback(this);
        }
    }

    @Override
    public final void onPreviewFrame(byte[] data, Camera camera) {
        // Size s = camera.getParameters().getPreviewSize();
        // System.out.println("!!!==============> " + s.width + " " + s.height);
        PreviewManager pm = this.camera.getPreviewManager();
        Size size = pm.getPreviewSize();
        int dataWidth = size.width;
        int dataHeight = size.height;
        bean.setSource(data, dataWidth, dataHeight);
        // frame
        int left, top, width, height;
        Viewfinder viewfinder = pm.getViewfinder();
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
        decode.setRequest(bean, this, viewfinder);// jump to decode
        thread.post(decode);
    }

    @Override
    public final void onDecodeSuccess(ScanResult result) {
        listener.onScanSuccess(result);
        camera.stopAutoFocus();
        bean.clearBuff();
        ready = true;
    }

    @Override
    public void onDecodeFail() {
        startScan();
    }

}
