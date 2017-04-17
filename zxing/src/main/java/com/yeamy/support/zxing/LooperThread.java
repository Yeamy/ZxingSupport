package com.yeamy.support.zxing;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;

public class LooperThread extends Thread {
    private Handler handler;
    private CountDownLatch handlerInitLatch;

    public LooperThread() {
        handlerInitLatch = new CountDownLatch(1);
    }

    private Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        handlerInitLatch.countDown();
        Looper.loop();
    }

    public void post(Runnable r) {
        getHandler().post(r);
    }

    public void postDelayed(Runnable r, long delayMillis) {
        getHandler().postDelayed(r, delayMillis);
    }

    public void removeCallbacks(Runnable r) {
        getHandler().removeCallbacks(r);
    }

    public void close() {
        post(new Runnable() {

            @Override
            public void run() {
                Looper looper = Looper.myLooper();
                if (looper != null) {
                    if (Build.VERSION.SDK_INT >= 18) {
                        looper.quitSafely();
                    } else {
                        looper.quit();
                    }
                }
            }
        });
    }

}