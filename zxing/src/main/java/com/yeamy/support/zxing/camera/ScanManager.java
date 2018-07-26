package com.yeamy.support.zxing.camera;

import android.graphics.Rect;

import com.yeamy.support.zxing.CameraShotListener;
import com.yeamy.support.zxing.LooperThread;
import com.yeamy.support.zxing.ScanResult;
import com.yeamy.support.zxing.ScanResultListener;
import com.yeamy.support.zxing.Size;
import com.yeamy.support.zxing.Viewfinder;
import com.yeamy.support.zxing.decode.DecodeBean;
import com.yeamy.support.zxing.decode.DecodeRequest;
import com.yeamy.support.zxing.decode.DecodeRequest.DecodeCallback;

public class ScanManager implements CameraShotListener, DecodeCallback {
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
            camera.setCameraShotListener(this);
        }
    }

    @Override
    public final void onCameraShot(byte[] data) {
        // Size s = camera.getParameters().getPreviewSize();
        // System.out.println("!!!==============> " + s.width + " " + s.height);
        PreviewManager pm = this.camera.getPreviewManager();
        Size size = pm.getPreviewSize();
        int dataWidth = size.width;
        int dataHeight = size.height;
        bean.setSource(data, dataWidth, dataHeight);
        // frame
        int left, top, vw, vh, width, height;
        Viewfinder viewfinder = pm.getViewfinder();
        Size view = viewfinder.getPreviewSize();
        vw = view.width;
        vh = view.height;
        Rect frame = viewfinder.getFrameRect();

        boolean portrait = viewfinder.getOrientation() % 180 != 0;
        if (portrait) {// portrait
            left = frame.top * dataWidth / vh;
            top = (vw - frame.right) * dataWidth / vh;
            width = frame.height() * dataWidth / vh;
            height = frame.width() * dataWidth / vh;
            bean.setFrameRectPortrait(left, top, width, height);
        } else {// landspace
            left = frame.left * dataWidth / vw;
            top = frame.top * dataWidth / vw;
            width = frame.width() * dataWidth / vw;
            height = frame.height() * dataWidth / vw;
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
