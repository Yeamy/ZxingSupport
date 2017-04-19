package com.yeamy.support.zxing.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.TextureView;

import com.yeamy.support.zxing.Viewfinder;

import java.util.List;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;
import static com.yeamy.support.zxing.Viewfinder.MIN_PREVIEW_PIXELS;

@SuppressWarnings("deprecation")
public final class CameraImpl {

    Camera device;
    Viewfinder viewfinder;
    Size previewSize;

    private AutoFocusManager autoFocus;
    private boolean af;
    private TextureView previewView;

    public CameraImpl() {
    }

    public boolean open() {
        return open(-1);
    }

    public boolean open(int cameraId) {
        Camera camera;
        if (cameraId >= 0) {
            camera = Camera.open(cameraId);
        } else {
            camera = Camera.open();
        }
        this.device = camera;
        return camera != null;
    }

    public boolean isOpen() {
        return this.device != null;
    }

    public void setAutoFocus(AutoFocusManager afm) {
        this.autoFocus = afm;
        afm.init(this.device);
    }

    private boolean isAutoFocusRunning() {
        return isPreviewing() && af && autoFocus != null;
    }

    void startAutoFocus() {
        af = true;
        if (isPreviewing() && autoFocus != null) {
            autoFocus.start();
        }
    }

    void stopAutoFocus() {
        af = false;
        if (autoFocus != null) {
            autoFocus.stop();
        }
    }

    public boolean supportTorch() {
        Parameters params = device.getParameters();
        List<String> modes = params.getSupportedFlashModes();
        return modes != null && modes.contains(FLASH_MODE_TORCH);
    }

    public void setTorch(boolean on) {
        boolean af = isAutoFocusRunning();
        if (af) {
            stopAutoFocus();
        }
        Parameters params = device.getParameters();
        params.setFlashMode(on ? FLASH_MODE_TORCH : FLASH_MODE_OFF);
        device.setParameters(params);
        if (af) {
            startAutoFocus();
        }
    }

    public void close() {
        if (device != null) {
            stopPreview();
            device.release();
            device = null;
        }
    }

    public boolean isPreviewing() {
        return viewfinder != null;
    }

    public void requestPreview(TextureView pv, Viewfinder vf) {
        this.viewfinder = vf;
        this.previewView = pv;
        setPreviewSize(vf);
        Listener l = new Listener();
        if (pv.isAvailable()) {
            l.onSurfaceTextureAvailable(pv.getSurfaceTexture(), 0, 0);
        }
        pv.setSurfaceTextureListener(l);
        device.setDisplayOrientation(vf.getOrientation());
    }

    private void setPreviewSize(Viewfinder vf) {
        Parameters params = device.getParameters();
        List<Size> list = params.getSupportedPreviewSizes();
        if (list == null || list.size() == 0) {
            return;
        }
        // ---------- plan
        Point plan = vf.getPreviewSize();
        int planWidth = plan.x;
        int planHeight = plan.y;
        //default is land
        if (vf.getOrientation() % 180 != 0) {
            int tmp = planWidth;
            planWidth = planHeight;
            planHeight = tmp;
        }
        int planPix = planWidth * planHeight;
        // ---------- default
        Size defaultSize = params.getPreviewSize();
        // ----------- find best
        int maxPx = defaultSize.width * defaultSize.height;
        Size maxSize = defaultSize;
        for (Size size : list) {
            int width = size.width, height = size.height;
            if (width > planWidth || height > planHeight) {
                continue;
            }
            int px = width * height;
            if (planPix >= px && px >= MIN_PREVIEW_PIXELS && px > maxPx) {
                maxSize = size;
                maxPx = px;
            }
        }
        if (maxSize != defaultSize) {
            params.setPreviewSize(maxSize.width, maxSize.height);
            device.setParameters(params);
        }
        previewSize = maxSize;
    }

    private void startPreview(SurfaceTexture surface, Viewfinder vf) {
        viewfinder = vf;
        try {
            device.setPreviewTexture(surface);
            device.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        stopAutoFocus();
        device.stopPreview();
        if (previewView != null) {
            previewView.setSurfaceTextureListener(null);
            previewView = null;
        }
        viewfinder = null;
    }

    private class Listener implements TextureView.SurfaceTextureListener {

        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            System.out.println("onSurfaceTextureAvailable---------->");
            Viewfinder vf = viewfinder;
            startPreview(surface, vf);
            if (vf.getOrientation() % 180 == 0) {
                vf.onStartPreview(previewView, previewSize.width, previewSize.height);
            } else {
                vf.onStartPreview(previewView, previewSize.height, previewSize.width);
            }
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Ignored, Camera does all the work for us
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            stopPreview();
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // Invoked every time there's a new Camera preview frame
        }
    }

}
