package com.yeamy.support.zxing.camera;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.TextureView;

import com.yeamy.support.zxing.Viewfinder;

import java.util.List;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

@SuppressWarnings("deprecation")
public final class CameraImpl {

    private Camera device;

    private PreviewManager previewManager = new PreviewManager();
    private AutoFocusManager autoFocus;
    private boolean af;

    public PreviewManager getPreviewManager() {
        return previewManager;
    }

    public Camera getDevice() {
        return device;
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
        return previewManager.isPreviewing() && af && autoFocus != null;
    }

    void startAutoFocus() {
        af = true;
        if (previewManager.isPreviewing() && autoFocus != null) {
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
            stopAutoFocus();
            previewManager.stopPreview();
            device.release();
            device = null;
        }
    }

    public void requestPreview(TextureView pv, Viewfinder vf) {
        previewManager.requestPreview(device, pv, vf);
    }

}