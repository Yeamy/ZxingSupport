package com.yeamy.support.zxing.camera;

import android.hardware.Camera;
import android.util.Log;

import com.yeamy.support.zxing.LooperThread;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Code come from zxing-android with little modification, can be replay by the original file;
 */
@SuppressWarnings("deprecation")
public final class AutoFocusManager implements Camera.AutoFocusCallback, Runnable {

    private static final String TAG = AutoFocusManager.class.getSimpleName();

    private static final long AUTO_FOCUS_INTERVAL_MS = 2000L;
    private static final Collection<String> FOCUS_MODES_CALLING_AF;

    static {
        FOCUS_MODES_CALLING_AF = new ArrayList<>(2);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
    }

    private boolean stopped = true;
    private boolean focusing = false;
    private boolean useAutoFocus;
    private Camera camera;
    private LooperThread thread;

    public AutoFocusManager(LooperThread thread) {
        this.thread = thread;
    }

    void init(Camera camera) {
        this.camera = camera;
        String currentFocusMode = camera.getParameters().getFocusMode();
        useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
        Log.i(TAG, "Current focus mode '" + currentFocusMode + "'; use auto focus? " + useAutoFocus);
    }

    @Override
    public synchronized void onAutoFocus(boolean success, Camera theCamera) {
        focusing = false;
        autoFocusAgainLater();
    }

    private synchronized void autoFocusAgainLater() {
        if (!stopped) {
            thread.postDelayed(this, AUTO_FOCUS_INTERVAL_MS);
        }
    }

    public synchronized void start() {
        stopped = false;
        run();
    }

    public synchronized void stop() {
        stopped = true;
        if (useAutoFocus) {
            thread.removeCallbacks(this);
            // Doesn't hurt to call this even if not focusing
            try {
                camera.cancelAutoFocus();
            } catch (RuntimeException re) {
                // Have heard RuntimeException reported in Android 4.0.x+; continue?
                Log.w(TAG, "Unexpected exception while cancelling focusing", re);
            }
        }
    }

    @Override
    public synchronized void run() {
        if (useAutoFocus && !stopped && !focusing) {
            try {
                camera.autoFocus(this);
                focusing = true;
            } catch (RuntimeException re) {
                // Have heard RuntimeException reported in Android 4.0.x+; continue?
                Log.w(TAG, "Unexpected exception while focusing", re);
                // Try again later to keep cycle going
                autoFocusAgainLater();
            }
        }
    }

}
