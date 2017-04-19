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
public final class PreviewManager {

    private Camera device;
    private Viewfinder viewfinder;
    private Size previewSize;
    private TextureView previewView;

    public Size getPreviewSize() {
        return previewSize;
    }

    public Viewfinder getViewfinder() {
        return viewfinder;
    }

    public boolean isPreviewing() {
        return viewfinder != null;
    }

    public void requestPreview(Camera device, TextureView pv, Viewfinder vf) {
        this.device = device;
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
        device.stopPreview();
        if (previewView != null) {
            previewView.setSurfaceTextureListener(null);
            previewView = null;
        }
        viewfinder = null;
    }

    private class Listener implements TextureView.SurfaceTextureListener {

        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
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
